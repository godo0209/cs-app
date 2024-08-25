'use strict'

const port = process.env.PORT || 3000

const express = require('express');
const logger = require('morgan');
const mongojs = require('mongojs');

const app = express();

var db = mongojs('CS');
var id = mongojs.ObjectId;

//middlewares
    app.use(logger('dev'));
    app.use(express.json({limit:'50mb'}));

//endpoints
    //files
        //gets
            app.get('/files', (req, res, next)=>{
                db.files.find({}, (err, resUsers)=>{
                    if(err) return next();
                    res.json(resUsers);
                });
            });
            app.get('/files/ids/:userId', (req, res, next) =>{   //devuelve los ids de todos los archivos
                console.log(req.params.userId);
                db.files.find({'userId':req.params.userId},{
                    '_id' :1,
                    'name' :1,
                    'extension' :1,
                    'date' :1
                },(err, files)=>{
                    if(err) return next(err);
                    res.json(files);
                });
            });
            app.get('/files/:id', (req, res, next) =>{   //devuelve el archivo
                let fileId = req.params.id
                db.files.findOne({_id:id(fileId)},(err, file)=>{
                    if(err) return next({
                        'result':'KO',
                        'err':err
                    });
                    res.json(file);
                });
            });
        //post
            app.post('/files', (req,res,next)=>{
                createFile(req.body).then(file_en=>{
                    db.files.save(file_en, (err, fileSave)=>{
                        if(err) return next({
                            'result':'KO',
                            'err':err
                        });
                        res.json({
                            'result':'OK',
                            'id' : fileSave._id
                        });
                    });
                });
            });
        //put
            app.put('/files/:id', (req, res, next)=>{
                let fileId = req.params.id;
                db.files.findOne({_id:id(fileId)}, (err, file)=>{
                    if(err)
                        return next({
                            'result':'KO',
                            'err': err
                        });
                    console.log("1");
                    db.files.update({_id:id(fileId)}, {$set: {'userId':req.body.userId}}, {safe :true, multi :false},
                    (err, resUserMod) =>{
                        if(err) return next(err);
                        res.json({'result':'OK',});
                    });
                });
            });
        //delete
            app.delete('/files', (req,res,next)=>{
                db.files.remove({},(err, res_db)=>{
                    res.json(res_db);
                });
            });
            app.delete('/files/:id', (req,res,next)=>{
                db.files.remove({_id:id(req.params.id)},(err, res_db)=>{
                    if(err)
                        res.json({'result':'KO'});
                    res.json({'result':'OK'})
                });
            });
    //users
        //gets
            app.get('/users', (req, res, next)=>{
                db.users.find({}, (err, resUsers)=>{
                    if(err) return next();
                    res.json(resUsers);
                });
            });
            app.get('/user/:id', (req, res, next)=>{    //devuelve un usuario
                let userId = req.params.id;
                db.users.findOne({_id:id(userId)}, (err, user)=>{
                    if(err) return next();
                    res.json(user);
                });
            });
            app.get('/user/id/:name', (req, res, next)=>{    //devuelve el id de un usuario
                let userName = req.params.name;
                db.users.findOne({'name':userName}, {_id:1}, (err, user)=>{
                    if(err) return next();
                    if(user == null)
                        res.json({
                            'result':'KO'
                        })
                    else
                        res.json({
                            'result':'OK',
                            'id': user._id
                        });
                });
            });
            app.get('/user/salt/:name', (req, res, next)=>{    //devuelve el salt
                let username = req.params.name;
                db.users.findOne({'name':username}, (err, salt)=>{
                    if(err) res.json({'result':'KO'});
                    if(salt != null)
                        res.json({
                            'result':'OK',
                            'salt':salt.salt
                        });
                    else
                        res.json({'result':'KO'});
                });
            });
            app.get('/user/publicKey/:id', (req, res, next)=>{    //devuelve la clave publica de un usuario
                let userId = req.params.id;
                db.users.findOne({_id:id(userId)}, {'publicKeyRSA':1},(err, user)=>{
                    if(err) return next();
                    res.json(user);
                });
            });
            app.get('/user/friends/:id', (req, res, next)=>{
                let userId = req.params.id;
                db.users.findOne({_id:id(userId)}, {'friends':1, _id:0},(err, friends)=>{
                    if(err) return next();
                    if(friends.friends != null){
                        res.json({
                            'result':'OK',
                            'friends':friends.friends
                        });
                    }
                    else{
                        res.json({
                            'result':'KO'
                        });
                    }
                });
            });
        //posts
            app.post('/user/login', (req,res,next)=>{
                db.users.findOne({'name':req.body.name}, (err, resUser)=>{
                    if(!resUser){
                        res.json({
                            'result':'KO',
                            'err':"User not found"
                        });
                    }
                    else{
                        console.log("1: " + resUser.password + "/ 2:" + req.body.password);
                        if(resUser.password == req.body.password){
                            res.json({
                                'result':'OK',
                                'id' : resUser._id
                            });
                        }
                        else{
                            res.json({
                                'result':'KO',
                                'err' : "Bad key"
                            });
                        }
                    }
                });
            });
            app.post('/user/register', (req,res,next)=>{
                createUser(req.body).then(user=>{
                    db.users.findOne({'name':user.name}, (err, resUser)=>{
                        if(resUser){
                            res.json({   //se ha encontrado el nombre
                                'result':'KO',
                                'err': "Invalid userName"
                            });
                        }
                        else{
                            db.users.save(user, (err, resUser2)=>{
                                if(err) return next({
                                    'result':'KO',
                                    'err':err
                                });
                                res.json({
                                    'result':'OK',
                                    'id' : resUser2._id
                                });
                            });
                        }
                        
                    });
                });
            });
        //put
            app.put('/user/:id/friends', (req, res, next)=>{
                let userId = req.params.id;
                db.users.findOne({_id:id(userId)}, {'friends':1}, (err, friends)=>{
                    if(err)
                        return next({
                            'result':'KO',
                            'err': err
                        });
                    createFriends(friends.friends, req.body.newFriend).then(friends=>{
                        db.users.update({_id:id(userId)}, {$set: {'friends':friends}}, {safe :true, multi :false},
                        (err, resUserMod) =>{
                            if(err) return next(err);
                            res.json({'result':'OK',});
                        });
                    });
                });
            });
        //delete
            app.delete('/user', (req,res,next)=>{
                db.users.remove({},(err, res_db)=>{
                    res.json(res_db);
                });
            });
    //notifications
        //gets
            app.get('/notifications', (req, res, next)=>{
                db.notifications.find({}, (err, result)=>{
                    if(err) return next();
                    res.json(result);
                })
            });
            app.get('/notifications/number/:id', (req, res, next)=>{
                db.notifications.find({userReciber : req.params.id}, (err, result)=>{
                    if(err) return next();
                    res.json({ 'notis':Object.keys(result).length });
                })
            });
            app.get('/notifications/:id', (req, res, next)=>{
                db.notifications.find({userReciber : req.params.id}, (err, result)=>{
                    if(err) return next();
                    if(result[0] == null)
                        res.json({'result':'KO'});
                    else
                        res.json({
                            'result':'OK',
                            'notis':result
                        })
                })
            });
        //post
            app.post('/notifications', (req, res, next)=>{
                createNoti(req.body).then(noti=>{
                    db.notifications.find({
                        userReciber:noti.userReciber,
                        userSender: noti.userSender
                    }, (err, result)=>{
                        if(result.length < 5){
                            db.notifications.save(noti, (err, result)=>{
                                if(err) return next();
                                res.json({'result':'OK'});
                            });
                        }
                        else{
                            db.files.remove({_id:id(noti.file)});
                            res.json({
                                'result':'KO',
                                'err': 'Too manny notifications'
                            });
                        }
                    })
                })
            });
        //delete
            app.delete('/notifications', (req,res,next)=>{
                db.notifications.remove({},(err, res_db)=>{
                    res.json(res_db);
                });
            });
            app.delete('/notifications/:id', (req,res,next)=>{
                db.notifications.remove({_id:id(req.params.id)},(err, res_db)=>{
                    res.json(res_db);
                });
            });
            app.delete('/notifications/file/:id', (req,res,next)=>{
                db.notifications.remove({fileId:req.params.id},(err, res_db)=>{
                    if(err)
                        res.json({'result':'KO'});
                    res.json({'result':'OK'})
                });
            });

app.listen(port);

function createFile(datos){
    return new Promise(resolve=>{
        let hoy = Date(Date.now),
        aux = hoy.split(' ', 5),
        txt = '';

        aux.forEach((e, i)=>{
            if(i != 0)
                txt += e+' ';
        });

        let file = {
            name : datos.name,
            date : txt,
            extension : datos.extension,
            data_en : datos.data_en,
            keyAES : datos.keyAES,
            userId: datos.userId
        }
        resolve(file);
    });
}
function createUser(datos){
    return new Promise(resolve=>{
        let user = {
            name : datos.name,
            password : datos.password,
            salt : datos.salt,
            publicKeyRSA : datos.publicKeyRSA,
            privateKeyRSA : datos.privateKeyRSA,
            keyAuth: datos.keyAuth
        }
        resolve(user);
    });
}

function createNoti(datos){
    return new Promise(resolve=>{
        let noti = {
            'userSender': datos.userSender,
            'userReciber':datos.userReciber,
            'file': datos.file
        }
        resolve(noti);
    })
}
function createFriends(friends, newFriend){
    return new Promise(resolve=>{
        let ret = [];
        if(friends){
            for(let i = 0; i < friends.length; i++){
                ret.push(friends[i]);
            }
        }
        ret.push(newFriend);
        resolve(ret);
    });
}