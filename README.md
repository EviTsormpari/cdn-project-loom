# Content Delivery Network (CDN) using Project Loom
This project presents the design and implementation of a **Content Delivery Network (CDN)** system built with **Java** using **Project Loom** for lightweight thread virtualization. The goal of this project is to explore and demonstrate the efficiency and scalability improvements introduced by Project Loom in Java-based distributed systems.
To achieve this, a CDN was developed to show how virtual threads improve responsiveness, concurrency and resource usage in real-world applications.

---

## ⚙️ Sytem Architecture
The system consists of several core entities, each responsible for a specific function in the CDN workflow.

### 🖥️ Origin Server
- Stores the **original content**.
- When content is updated or deleted, it automatically **informs all edge caches** to synchronize with it.

### 🌍 Edge Servers (2 instances)
- Retrieve content from origin **only if it is not available in cache**.
- Use a **Time-To-Live (TTL)** mechanism for content expiration.  
- Employ an **LRU (Least Recently Used)** policy for cache space management.
- Serve cached content to clients to minimize latency and reduce origin load.

  ### ⚖️ Load Balancer
  - The **Nginx load balancer** distributes user requests across edge servers.
  - Implements the **Round Robin** algorithm to ensure load distribution and high availability.
 
  ## 🛠 Technologies Used
  - **Project Loom**
  - Java 21+
  - Spring Boot 3.4+
  - PostgreSQL
  - Redis
  - **Docker Compose**
 
  ## 📚 Author
  **[Paraskevi Tsormpari]**
  Thesis Project
  Department of Appliend Informatics, University of Macedonia.
