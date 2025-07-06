
## 🔐 Project Summary — Secure File Sharing System

This project is focused on experimenting with **real-world cryptography techniques** and building a distributed system for secure file sharing between users. It consists of a **Java desktop client**, a **Node.js REST API**, and a **MongoDB database**, all orchestrated using **Docker Compose**.

---

### 🧩 System Components

| Component     | Description |
|---------------|-------------|
| 💻 Java Client | Swing-based desktop app with 2FA login, file upload, download, and sharing |
| 🌐 Node.js API | RESTful backend using `mongojs`, responsible for user management, encryption handling, and file routing |
| 🗄️ MongoDB      | Stores users, hashed passwords, encrypted file data, and notifications |
| 🐳 Docker       | Runs MongoDB and API for local testing, easy setup |

---

### 🔁 Authentication Workflow

```text
User ➡️ Registers with username, password, and TOTP key
     ➡️ Password is hashed using Argon2 with a random salt
     ➡️ TOTP key stored securely and used during login
````

* **Password Security**: Uses **Argon2** hashing algorithm with unique salt per user.
* **2FA with TOTP**: Implements **Time-Based One-Time Passwords** (TOTP) using Google Authenticator. Verification is performed on login with a shared secret.

---

### 🔒 File Encryption Flow

```text
Client ➡️ Encrypts file with AES
       ➡️ AES key is encrypted with user RSA public key
       ➡️ API stores encrypted file + AES key in MongoDB
```

* **AES**: Used for encrypting the file contents (symmetric).
* **RSA**: Used for encrypting the AES key for each file (asymmetric).
* **MongoDB** stores the binary file data and encrypted AES key.

---

### 📬 Notifications & Friend System

* Users can share files with others users and mark them as "friends".
* The system sends a **notification** when a file is shared.
* Friends can **download and decrypt** shared files using their RSA private key.

---

### 🛠 Technologies Used

* Java (Swing GUI)
* MongoDB
* Node.js (`express`, `mongojs`)
* Docker & Docker Compose
* Argon2 password hashing
* AES + RSA encryption
* TOTP (Google Authenticator-compatible)

---

## 🌊 Workflows

Feel free to take a look at the [diagrams](./workflows.md) prepared to explain the workflow of the app, it may help to get a better undertanding of everything.

> 📘 If you want more information about the project I recommend you to read the [Technical report on encryption and authentication (PDF)](./docs/cryptography-report.pdf)
