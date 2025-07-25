
#   Project workflows

I used mermaid diagrams to explain the workflow I used for each action. First take a look at the state diagram beacause the next diagrams may make reference to that one in order to be more clear and separate workflows.

### 🗺️ States

In the next diagram you can see the states in which the user can move between

```mermaid
---
config:
  theme: redux
  look: neo
  layout: dagre
---
stateDiagram
  direction TB
  [*] --> Login
  [*] --> Register
  Login --> 2FA
  Register --> 2FA
  2FA --> Await
  Await --> Upload
  Await --> Download
  Await --> Send
```
---

### 👤 Auth

There are two diagrams, one for `Register` process and one for `Login`. Although both processes end up having the same information about the user each one have a different flow to reach it. 

<img src="./Auth-diagram.svg"  alt="Auth diagram"  />

---
## 🎯 Actions

The actions seen in the diagram are `Upload`, `Download` and `Send`. In the diagram `Send` the nodes `Encrypt File` and `Decrypt File` make references to the processes seen in the diagrams `Upload` and `Download`.

<img src="./Actions-diagram.svg"  alt="Actions diagram"  />


