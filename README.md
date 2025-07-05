# 🔐 CS-App — Cryptography Showcase (Local Demo)

This project is a **local-only demo** of a secure file-sharing system built with **Java**, **Node.js**, and **MongoDB**, designed primarily to **experiment with cryptographic concepts** like:

- 🔑 AES and RSA encryption for files  
- 🛂 2FA with Google Authenticator (TOTP)  
- 🧂 Argon2 password hashing with salt

> ⚠️ **Note**: This project was built for educational purposes around **cryptography**, not for production use. It intentionally **sacrifices accessibility, performance, and security best practices** to prioritize learning.

---

## 🔒 Features

* 🧑‍🤝‍🧑 Register and log in with 2FA (TOTP + password)
* 🪪 Argon2 password hashing + unique salts
* 📁 Upload/download/transfer files (AES + RSA encrypted)
* 🔔 Notifications when files are shared
* 👫 Friend list with user search and sharing support
---

## 🚨 Known Issues

This repository contains **known flaws** and should **never be used in production**. Some limitations include:

| Area          | Issue                                                            |
| ------------- | ---------------------------------------------------------------- |
| 🔐 Security   | ❌ No JWT/session handling, No API authentication or rate limitin|
| 📦 Storage    | ❌ Secured files are stored as raw bytes in database                |
| ⏱️ TOTP       | ❌ Has a \~2 second sync lag (no time skew tolerance)               |
| 🖼️ UI        | ❌ No accessibility features, no password masking, minimal feedback |

---

## 📂 Project Structure

```text
cs-app/
├── cs/                     # Node.js REST API
├── demo/
|   ├── src/                # App code
|   └── client-app.jar      # App client
├── runproject
|   └──src/                 # Launcher code
|   └──runproject.jar       # Launcher app to start the backend and client
├── docker-compose.yml      # Starts the API + MongoDB services
└── README.md
````

---

## 🚀 How to Run

### 🛠️ Prerequisites

* ✅ [Docker](https://www.docker.com/) + Docker Compose
* ✅ [Java 17+ installed](https://dev.java/download/)

---

### ▶️ Start the App

You can clone the repository or just [download the release V1.0.0](https://github.com/godo0209/cs-app/releases/download/v1.0.0/cs-app-release.zip) (if you downloaded the release please extract the zip into a folder).

>💡 From there you just double click the file `runproject.jar` and click the button that will appear. 

The file `runproject.jar` was created to make things easier and will run the services needed to work and also will open the app file for you. But if you prefer manual steps, run:

```
git clone https://github.com/godo0209/cs-app
cd cs-app

# Start backend (MongoDB + API)
docker compose up --build -d

cd demo

# Run the Java client manually
java -jar cs-app.jar
```

---

## 📘 About This Project

This was a university project focused on **learning modern cryptographic practices** in real-world applications. While it's far from production-ready, it showcases the following:

* Client-server communication
* Data encryption at rest and in transit
* Authentication via passwords + 2FA
* Dockerized local environment

---

## 📜 License

This project is licensed under the [MIT License](https://opensource.org/license/mit).
You're free to reuse or remix the code — just don't use it in production as-is.
