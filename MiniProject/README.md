# TCET Mumbai — Project Management System (PMS)
### Industrial Program Mini-Project | Full Stack Development using Java (SE-IT)

---

## 📌 1. Project Overview & Objectives
The **Project Management System (PMS)** is a lightweight, full-stack enterprise web application designed in strict accordance with the **Mumbai University (TCET) Second-Year IT syllabus** for Full Stack Development using Java.

It provides a collaborative workspace for engineering teams to:
- **Manage Projects**: Create, inspect, prioritize, and track project lifecycle stages.
- **Interactive Kanban Board**: Dynamic task progression across **To Do**, **In Progress**, and **Completed** columns with full HTML5 Drag & Drop support.
- **Team Resource Allocation**: Register users and assign roles (Lead Architect, Developer, QA, etc.).
- **Live Metrics Dashboard**: Visual KPIs computing real-time task completion ratios, active workload, and workspace capacity.

---

## 🏛️ 2. Architectural Blueprint & OOP Implementation

### System Architecture (Layered / MVC Pattern)
```
       [ Modern Browser / Web Client ]
       (HTML5 + CSS3 + Tailwind + Vanilla ES6 JS)
                      │
           HTTP / JSON REST API Calls
                      │
                      ▼
 ┌───────────────────────────────────────────────────┐
 │               Java HTTP Backend                   │
 │                                                   │
 │  1. Controller Layer:                             │
 │     - ApiHandler (REST Dispatcher, CORS)          │
 │     - StaticFileHandler (Static Web Asset Engine) │
 │                                                   │
 │  2. Service Layer:                                │
 │     - ProjectManagerService (Business Logic)      │
 │                                                   │
 │  3. Repository Layer (Data Access Pattern):       │
 │     - Repository<T> (Generic Interface)           │
 │     - ProjectRepository, TaskRepository,          │
 │       UserRepository (Thread-Safe Persistence)    │
 │                                                   │
 │  4. Domain Model Layer (OOP Core):                │
 │     - BaseEntity (Abstract Class)                 │
 │       ├── Project                                 │
 │       ├── Task                                    │
 │       └── User                                    │
 │     - Priority, Status (Enums)                    │
 │                                                   │
 │  5. Utilities:                                    │
 │     - SimpleJson (Zero-Dependency Parser)         │
 │     - ValidationUtil                              │
 └───────────────────────────────────────────────────┘
                      │
                      ▼
             [ JSON Flat-File Store ]
         (data/projects.json, tasks.json, users.json)
```

### Core OOP Principles Evaluated
| OOP Pillar | Code Realization | Description |
| :--- | :--- | :--- |
| **Abstraction** | `BaseEntity`, `Repository<T>` | High-level contracts define lifecycle operations (`findAll()`, `save()`, `deleteById()`) without binding the consumer to concrete storage details. |
| **Encapsulation** | `Project`, `Task`, `User` | Private state fields, immutable entity identifiers, defensive setters with validation checks, and automatic `updatedAt` tracking. |
| **Inheritance** | `BaseEntity -> Project, Task, User` | Shared lifecycle behavior (`id`, `createdAt`, `updatedAt`, `touch()`, `equals()`, `hashCode()`) is centralized in a base class. |
| **Polymorphism** | Method Overriding & Interface Dispatch | Common interface methods (`save`, `deleteById`) executed through runtime dynamic dispatch across various entities. |

### Java Collections Framework Usage
- **`List<T>` & `ArrayList<T>`**: Dynamic storage and ordered retrieval of entities.
- **`ConcurrentHashMap<String, T>`**: High-performance, thread-safe in-memory caching ensuring zero race conditions under concurrent HTTP hits.
- **Java Streams API (`filter`, `map`, `sorted`, `collect`)**: Functional data processing for status filtering, user lookups, and KPI statistics calculations.

---

## 📂 3. Project Directory Structure
```
d:\IT_program\IP\MiniProject\
├── bin/                              # Compiled Java bytecode (.class files)
├── data/                             # Persistent JSON data stores
│   ├── projects.json
│   ├── tasks.json
│   └── users.json
├── public/                           # Frontend Assets
│   ├── index.html                    # Single-Page Dashboard & Kanban UI
│   ├── css/
│   │   └── style.css                 # Custom styles, Kanban drag-over effects
│   └── js/
│       ├── api.js                    # REST API Client (Fetch Async/Await)
│       └── app.js                    # DOM Manager, Drag-and-Drop, State Machine
├── src/
│   └── com/
│       └── tcet/
│           └── pms/
│               ├── Main.java                 # Server Bootstrap & Thread Pool
│               ├── controller/
│               │   ├── ApiHandler.java       # REST Routing, CORS, JSON response
│               │   └── StaticFileHandler.java# Static web asset server
│               ├── model/
│               │   ├── BaseEntity.java       # Abstract Base Class
│               │   ├── Priority.java         # Priority Enumeration
│               │   ├── Project.java          # Project Domain Model
│               │   ├── Status.java           # Kanban Status Enumeration
│               │   ├── Task.java             # Task Domain Model
│               │   └── User.java             # User / Team Domain Model
│               ├── repository/
│               │   ├── FileDataStore.java    # Disk persistence engine
│               │   ├── ProjectRepository.java# Project repository implementation
│               │   ├── Repository.java       # Generic CRUD Interface
│               │   ├── TaskRepository.java   # Task repository implementation
│               │   └── UserRepository.java   # User repository implementation
│               ├── service/
│               │   └── ProjectManagerService.java # Business Logic Facade
│               └── util/
│                   ├── SimpleJson.java       # Zero-dependency JSON Engine
│                   └── ValidationUtil.java   # Validation rules
├── pom.xml                           # Optional Maven build configuration
├── run.bat                           # 1-Click Windows execution script
├── run.sh                            # Unix/Linux/macOS execution script
└── README.md                         # Project documentation and viva guide
```

---

## 🚀 4. How to Compile and Run

### Option A: 1-Click Launch (Recommended for Viva Presentation)
Simply double-click:
```bat
run.bat
```
*(This automatically creates directories, compiles all Java files with `javac`, starts the server on port `8080`, and opens your default browser at `http://localhost:8080`)*

---

### Option B: Manual Command-Line Compilation (For Professor Demo)
1. **Open PowerShell or Command Prompt** in the project directory:
   ```powershell
   cd d:\IT_program\IP\MiniProject
   ```

2. **Compile the Java source files**:
   ```powershell
   javac -encoding UTF-8 -d bin src\com\tcet\pms\model\*.java src\com\tcet\pms\repository\*.java src\com\tcet\pms\service\*.java src\com\tcet\pms\util\*.java src\com\tcet\pms\controller\*.java src\com\tcet\pms\Main.java
   ```

3. **Start the Application Server**:
   ```powershell
   java -cp bin com.tcet.pms.Main 8080
   ```

4. **Access the Web Application**:
   Open any modern browser and navigate to:
   ```
   http://localhost:8080
   ```

---

## 📡 5. REST API Documentation

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/health` | Server heartbeat and timestamp |
| `GET` | `/api/stats` | Aggregated metrics for dashboard counters |
| `GET` | `/api/projects` | List all registered projects |
| `POST` | `/api/projects` | Create a new project `{name, description, priority, owner}` |
| `DELETE` | `/api/projects/{id}` | Delete a project and its cascade tasks |
| `GET` | `/api/tasks?projectId={id}` | List tasks filtered by project |
| `POST` | `/api/tasks` | Create new task `{projectId, title, priority, assignee, status}` |
| `PUT` | `/api/tasks/{id}` | Update task details or status |
| `DELETE` | `/api/tasks/{id}` | Delete task by ID |
| `GET` | `/api/users` | List all engineering team members |
| `POST` | `/api/users` | Register team member `{name, email, role, department}` |
| `DELETE` | `/api/users/{id}` | Remove team member |

---

## 🎓 6. Viva Presentation Questions & Model Answers

### Q1: Why did you use Java's built-in `HttpServer` instead of standard Apache Tomcat or Spring Boot?
> **Answer**: Built-in `com.sun.net.httpserver.HttpServer` is a lightweight, native standard-library HTTP engine available in every standard JDK installation. It eliminates external runtime dependencies, allows our application to run natively using just `javac` and `java`, and provides full control over raw HTTP dispatching, CORS headers, thread pooling (`ExecutorService`), and REST routing.

### Q2: How did you implement Data Persistence without an external database like MySQL?
> **Answer**: We implemented the **Repository Pattern** backed by `ConcurrentHashMap` for high-throughput in-memory caching and `FileDataStore` using Java NIO (`java.nio.file.Files`) to persist records atomically in JSON format under the `data/` directory.

### Q3: How does the Drag-and-Drop feature synchronize with the Java Backend?
> **Answer**: We leverage the HTML5 Drag and Drop API (`dragstart`, `dragover`, `drop`). When a task card is dropped into a target column, the event listener reads the task ID, optimistically repositions the DOM element, and issues an asynchronous `PUT /api/tasks/{id}` request with the new status (`todo`, `inprogress`, or `completed`). The server validates the entity and commits the change to disk.

### Q4: Explain the use of Java Generics in your project.
> **Answer**: The interface `Repository<T extends BaseEntity>` utilizes bounded type parameters. This guarantees type safety during compilation so that any class implementing the repository contract (such as `ProjectRepository` or `TaskRepository`) only handles valid entity types without runtime type casting errors.
