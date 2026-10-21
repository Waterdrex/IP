package com.tcet.pms.service;

import com.tcet.pms.model.*;
import com.tcet.pms.repository.ProjectRepository;
import com.tcet.pms.repository.TaskRepository;
import com.tcet.pms.repository.UserRepository;
import com.tcet.pms.util.ValidationUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Layer & Facade handling business logic, validation, and analytics.
 * Demonstrates Service Pattern, Separation of Concerns, and Collections Stream API.
 */
public class ProjectManagerService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public ProjectManagerService() {
        this.projectRepository = new ProjectRepository();
        this.taskRepository = new TaskRepository();
        this.userRepository = new UserRepository();

        // Seed with meaningful initial data if completely empty
        seedInitialDataIfEmpty();
    }

    private void seedInitialDataIfEmpty() {
        if (projectRepository.count() == 0) {
            // Seed Users
            User u1 = new User("Aarav Sharma", "aarav.sharma@tcetmumbai.in", "Lead Architect", "IT Dept");
            User u2 = new User("Pooja Verma", "pooja.verma@tcetmumbai.in", "Full Stack Developer", "IT Dept");
            User u3 = new User("Rohan Patil", "rohan.patil@tcetmumbai.in", "QA & DevOps Engineer", "IT Dept");
            userRepository.save(u1);
            userRepository.save(u2);
            userRepository.save(u3);

            // Seed Projects
            Project p1 = new Project("College ERP Portal", "Full-stack student-faculty management platform with role-based access.");
            p1.setPriority(Priority.HIGH);
            p1.setOwner(u1.getName());
            projectRepository.save(p1);

            Project p2 = new Project("Smart Attendance AI", "Edge vision attendance automation for university lecture halls.");
            p2.setPriority(Priority.MEDIUM);
            p2.setOwner(u2.getName());
            projectRepository.save(p2);

            // Seed Tasks for Project 1
            Task t1 = new Task(p1.getId(), "Design System Architecture", "Draft UML class diagrams, database schema, and REST endpoints.", Priority.HIGH, u1.getName());
            t1.setStatus(Status.COMPLETED);

            Task t2 = new Task(p1.getId(), "Implement Core Java API", "Build HTTP request handlers and in-memory repository layer.", Priority.HIGH, u2.getName());
            t2.setStatus(Status.IN_PROGRESS);

            Task t3 = new Task(p1.getId(), "Connect Frontend Kanban", "Integrate HTML5 Drag-and-Drop board with REST API.", Priority.MEDIUM, u2.getName());
            t3.setStatus(Status.TODO);

            Task t4 = new Task(p1.getId(), "Prepare Viva Presentation", "Document OOP pillars, architecture flowchart, and run commands.", Priority.MEDIUM, u3.getName());
            t4.setStatus(Status.TODO);

            taskRepository.save(t1);
            taskRepository.save(t2);
            taskRepository.save(t3);
            taskRepository.save(t4);

            // Seed Tasks for Project 2
            Task t5 = new Task(p2.getId(), "Dataset Collection & Labeling", "Collect 500 face samples with consent for evaluation model.", Priority.MEDIUM, u3.getName());
            t5.setStatus(Status.COMPLETED);

            Task t6 = new Task(p2.getId(), "Integrate Camera Stream Handler", "Build low-latency RTSP video ingestion pipeline.", Priority.HIGH, u1.getName());
            t6.setStatus(Status.IN_PROGRESS);

            taskRepository.save(t5);
            taskRepository.save(t6);
        }
    }

    // ================= PROJECTS =================

    public List<Map<String, Object>> getAllProjects() {
        return projectRepository.findAll().stream()
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .map(Project::toMap)
                .collect(Collectors.toList());
    }

    public Optional<Project> getProjectById(String id) {
        return projectRepository.findById(id);
    }

    public Project createProject(Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String desc = (String) payload.get("description");
        String priorityStr = (String) payload.get("priority");
        String owner = (String) payload.get("owner");

        ValidationUtil.requireNotEmpty(name, "Project name");

        Project project = new Project(name.trim(), desc != null ? desc.trim() : "");
        if (priorityStr != null) {
            project.setPriority(Priority.fromCode(priorityStr));
        }
        if (owner != null) {
            project.setOwner(owner.trim());
        }
        return projectRepository.save(project);
    }

    public boolean deleteProject(String id) {
        // Cascade delete all tasks belonging to this project
        taskRepository.deleteByProjectId(id);
        return projectRepository.deleteById(id);
    }

    // ================= TASKS =================

    public List<Map<String, Object>> getAllTasks(String projectId) {
        List<Task> tasks;
        if (projectId != null && !projectId.trim().isEmpty()) {
            tasks = taskRepository.findByProjectId(projectId.trim());
        } else {
            tasks = taskRepository.findAll();
        }
        return tasks.stream()
                .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                .map(Task::toMap)
                .collect(Collectors.toList());
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public Task createTask(Map<String, Object> payload) {
        String title = (String) payload.get("title");
        String projectId = (String) payload.get("projectId");
        String desc = (String) payload.get("description");
        String statusStr = (String) payload.get("status");
        String priorityStr = (String) payload.get("priority");
        String assignee = (String) payload.get("assignee");

        ValidationUtil.requireNotEmpty(title, "Task title");
        ValidationUtil.requireNotEmpty(projectId, "Project ID");

        Task task = new Task(projectId.trim(), title.trim(), desc != null ? desc.trim() : "",
                Priority.fromCode(priorityStr), assignee != null ? assignee.trim() : "");
        if (statusStr != null) {
            task.setStatus(Status.fromCode(statusStr));
        }
        return taskRepository.save(task);
    }

    public Optional<Task> updateTask(String id, Map<String, Object> payload) {
        Optional<Task> existingOpt = taskRepository.findById(id);
        if (!existingOpt.isPresent()) {
            return Optional.empty();
        }
        Task task = existingOpt.get();

        if (payload.containsKey("title")) {
            String title = (String) payload.get("title");
            if (ValidationUtil.isNotEmpty(title)) {
                task.setTitle(title.trim());
            }
        }
        if (payload.containsKey("description")) {
            task.setDescription((String) payload.get("description"));
        }
        if (payload.containsKey("status")) {
            task.setStatus(Status.fromCode((String) payload.get("status")));
        }
        if (payload.containsKey("priority")) {
            task.setPriority(Priority.fromCode((String) payload.get("priority")));
        }
        if (payload.containsKey("assignee")) {
            task.setAssignee((String) payload.get("assignee"));
        }
        task.touch();
        taskRepository.save(task);
        return Optional.of(task);
    }

    public boolean deleteTask(String id) {
        return taskRepository.deleteById(id);
    }

    // ================= USERS =================

    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getName))
                .map(User::toMap)
                .collect(Collectors.toList());
    }

    public User createUser(Map<String, Object> payload) {
        String name = (String) payload.get("name");
        String email = (String) payload.get("email");
        String role = (String) payload.get("role");
        String department = (String) payload.get("department");

        ValidationUtil.requireNotEmpty(name, "User name");
        ValidationUtil.requireNotEmpty(email, "User email");

        User user = new User(name.trim(), email.trim(), role, department);
        return userRepository.save(user);
    }

    public boolean deleteUser(String id) {
        return userRepository.deleteById(id);
    }

    // ================= DASHBOARD ANALYTICS =================

    public Map<String, Object> getDashboardStats() {
        long totalProjects = projectRepository.count();
        List<Task> allTasks = taskRepository.findAll();
        long activeTasks = allTasks.stream().filter(t -> t.getStatus() != Status.COMPLETED).count();
        long completedTasks = allTasks.stream().filter(t -> t.getStatus() == Status.COMPLETED).count();
        long totalUsers = userRepository.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProjects", totalProjects);
        stats.put("totalTasks", allTasks.size());
        stats.put("activeTasks", activeTasks);
        stats.put("completedTasks", completedTasks);
        stats.put("totalUsers", totalUsers);
        return stats;
    }
}
