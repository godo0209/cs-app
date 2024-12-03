
# What is cs-app?
This is a proyect made for a 3º grade subject of Multimedia engineering while studing at University of Alicante.
The main point in this activity was to learn how to use simetric and asimetric cryptography as well as been able to hash correctly the password of our users.

## What I did?
In order to complete this task I created an [api](https://github.com/godo0209/cs-app/blob/main/cs/index.js) in JavaScript, that was connected with a non-relational database supported by MongoDB.
I also created a [client](https://github.com/godo0209/cs-app/tree/main/demo/src/main/java/com/example) in Java, making use of Maven, where all the cryptography is done and that make the calls to the api. 
It wasn't necesary to do it like that, however, I wanted to prepeare the system in order to been able to have more than one client, having more flexibility.

# How is works?

## Login / Register process
As soon as the user gets in, he will see two inputs, asking for a username and a password, and two buttons, one for login and one for registration. If both inputs meet some requirements then the user can register or login.

### Login
If the user wants to log in, the program will to call the api for the hash in the database for that username, and will hash the password given in the input. After checking if the api password is the same as the second half of the user hashed passor