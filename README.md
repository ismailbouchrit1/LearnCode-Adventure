# 🎮 LearnCode Adventure — Spring Boot

> **Jeu éducatif Python interactif** pour les élèves du tronc commun.
> 3 mini-jeux (Puzzle, Runner Quiz, Memory) avec classement et badges.

| Tech | Version |
|------|---------|
| Java | 21 (LTS) |
| Spring Boot | 3.3.5 |
| Template Engine | Thymeleaf |
| Database | H2 (embedded) |
| Build Tool | Maven |

---

## 📑 Table of Contents

- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Local Development Setup](#-local-development-setup)
- [Prepare for Online Deployment](#-prepare-for-online-deployment)
  - [Step 1 — Add Maven Wrapper](#step-1--add-maven-wrapper)
  - [Step 2 — Add .gitignore](#step-2--add-gitignore)
  - [Step 3 — Create Production Profile](#step-3--create-production-profile)
  - [Step 4 — Add a Dockerfile (Optional)](#step-4--add-a-dockerfile-optional)
  - [Step 5 — Initialize Git Repository](#step-5--initialize-git-repository)
  - [Step 6 — Push to GitHub](#step-6--push-to-github)
- [Deploy Online (3 Options)](#-deploy-online-3-options)
  - [Option A — Railway (Recommended, Easiest)](#option-a--railway-recommended-easiest)
  - [Option B — Render (Free Tier)](#option-b--render-free-tier)
  - [Option C — Azure App Service (Student Credits)](#option-c--azure-app-service-student-credits)
- [Production Configuration Reference](#-production-configuration-reference)
- [Troubleshooting](#-troubleshooting)
- [The 3 Games](#-the-3-games)

---

## 📁 Project Structure

```
LearnCodeAdventure-SpringBoot/
├── pom.xml                                  ← Maven build config
├── README.md                                ← This file
├── src/main/
│   ├── java/com/learncode/
│   │   ├── LearnCodeApplication.java        ← Entry point (main class)
│   │   ├── controller/
│   │   │   ├── MainController.java          ← HTML page routes
│   │   │   └── ApiController.java           ← REST API (game data)
│   │   ├── model/Student.java               ← Student JPA entity
│   │   ├── repository/StudentRepository.java← Data access layer
│   │   └── service/GameService.java         ← Game logic & data
│   └── resources/
│       ├── application.properties           ← App configuration
│       ├── templates/                       ← Thymeleaf HTML pages
│       │   ├── index.html                   ← Home + character select
│       │   ├── menu.html                    ← Main menu
│       │   ├── game_puzzle.html             ← Puzzle game
│       │   ├── game_runner.html             ← Runner quiz game
│       │   ├── game_memory.html             ← Memory game
│       │   ├── leaderboard.html             ← Leaderboard
│       │   └── fragments.html               ← Reusable HUD fragment
│       └── static/
│           ├── css/                         ← Stylesheets
│           └── js/                          ← Game JavaScript
```

---

## ✅ Prerequisites

Before anything, make sure you have:

| Tool | Required Version | Download |
|------|-----------------|----------|
| **Java JDK** | 21 or newer | [Adoptium (Eclipse Temurin)](https://adoptium.net) |
| **Git** | Any recent version | [git-scm.com](https://git-scm.com) |
| **GitHub Account** | — | [github.com](https://github.com) |

Verify your installations:

```bash
java -version
# → openjdk version "21.x.x" or newer

git --version
# → git version 2.x.x
```

> **Note:** Maven is **not** required to be installed globally — we will add the Maven Wrapper to the project so hosting platforms can build automatically.

---

## 🖥️ Local Development Setup

### 1. Clone or download the project

```bash
git clone https://github.com/YOUR_USERNAME/LearnCodeAdventure-SpringBoot.git
cd LearnCodeAdventure-SpringBoot
```

### 2. Run the application

**On Windows (PowerShell):**
```powershell
.\mvnw.cmd spring-boot:run
```

**On macOS / Linux:**
```bash
./mvnw spring-boot:run
```

### 3. Open in your browser

```
http://localhost:8080
```

The H2 database is created automatically on first launch with demo data (7 sample students).

### H2 Database Console (dev only)

```
http://localhost:8080/h2-console
```
| Field | Value |
|-------|-------|
| JDBC URL | `jdbc:h2:file:./learncode_db` |
| User | `sa` |
| Password | *(leave empty)* |

---

## 🚀 Prepare for Online Deployment

Follow **all 6 steps** in order before deploying.

---

### Step 1 — Add Maven Wrapper

The Maven Wrapper lets hosting services build your project **without having Maven pre-installed**.

Run this command from the project root (you need Maven installed locally for this one-time step, or use IntelliJ's bundled Maven):

**If you have Maven installed globally:**
```bash
mvn wrapper:wrapper -Dmaven=3.9.9
```

**If you don't have Maven installed** — use IntelliJ IDEA:
1. Open the project in IntelliJ
2. Go to **Terminal** (bottom panel)
3. Run:
   ```bash
   mvn wrapper:wrapper -Dmaven=3.9.9
   ```
   IntelliJ uses its bundled Maven, so this will work.

**Alternative — download wrapper files manually:**
```bash
# Windows PowerShell
Invoke-WebRequest -Uri "https://raw.githubusercontent.com/apache/maven-wrapper/main/maven-wrapper-distribution/src/resources/.mvn/wrapper/maven-wrapper.properties" -OutFile ".mvn/wrapper/maven-wrapper.properties"
```

After this step you should have these new files:
```
LearnCodeAdventure-SpringBoot/
├── mvnw              ← Unix/Mac build script
├── mvnw.cmd          ← Windows build script
└── .mvn/
    └── wrapper/
        └── maven-wrapper.properties
```

> ⚠️ **Important (Linux/Mac):** Make the wrapper executable:
> ```bash
> chmod +x mvnw
> ```

---

### Step 2 — Add `.gitignore`

Create a file named **`.gitignore`** in the project root with this content:

```gitignore
# === Build output ===
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

# === IDE files ===
.idea/
*.iml
*.iws
*.ipr
.vscode/
.settings/
.project
.classpath
*.code-workspace

# === H2 database files ===
*.mv.db
*.trace.db

# === OS junk ===
.DS_Store
Thumbs.db
desktop.ini

# === Logs ===
*.log
logs/

# === Misc ===
*.swp
*.swo
*~
```

---

### Step 3 — Create Production Profile

Your current `application.properties` is configured for **local development** (H2 file-based database, H2 console enabled). For production, create an additional profile.

Create a new file: **`src/main/resources/application-prod.properties`**

```properties
# ══ LearnCode Adventure — PRODUCTION ══

# Use the PORT environment variable assigned by the hosting platform
server.port=${PORT:8080}

# H2 — In-memory database for production (no file system dependency)
spring.datasource.url=jdbc:h2:mem:learncode_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Disable H2 web console in production (security)
spring.h2.console.enabled=false

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Thymeleaf — enable cache in production
spring.thymeleaf.cache=true

# Logs
logging.level.com.learncode=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
```

> **Why in-memory H2?** Most free-tier hosting platforms use **ephemeral file systems** — files are deleted on every redeploy. An in-memory database avoids file permission issues. Your demo data is inserted automatically by `LearnCodeApplication.java` on every cold start.

---

### Step 4 — Add a Dockerfile (Optional)

A Dockerfile is **required for Railway** and useful for other platforms. Create a file named **`Dockerfile`** in the project root:

```dockerfile
# ── Stage 1: Build ──
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy Maven Wrapper and POM first (layer caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ── Stage 2: Run ──
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (platforms override via $PORT)
EXPOSE 8080

# Activate production profile
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
```

> This is a **multi-stage build** — the final image contains only the JRE + your JAR (~200 MB instead of ~500 MB).

---

### Step 5 — Initialize Git Repository

```bash
cd LearnCodeAdventure-SpringBoot

git init
git add .
git commit -m "Initial commit: LearnCode Adventure Spring Boot"
```

---

### Step 6 — Push to GitHub

1. Go to [github.com/new](https://github.com/new)
2. Create a **new repository** named `LearnCodeAdventure-SpringBoot`
3. Set it to **Public** (required for free-tier hosting)
4. **Do NOT** initialize with README, .gitignore, or license (you already have them)
5. Click **Create repository**
6. Run the commands GitHub shows you:

```bash
git remote add origin https://github.com/YOUR_USERNAME/LearnCodeAdventure-SpringBoot.git
git branch -M main
git push -u origin main
```

> Replace `YOUR_USERNAME` with your actual GitHub username.

---

## 🌍 Deploy Online (3 Options)

Choose the platform that works best for you:

| Platform | Free Tier | Ease | Sleep? | Custom Domain |
|----------|-----------|------|--------|---------------|
| **Railway** | $5 free credit/month | ⭐⭐⭐⭐⭐ | No | Yes |
| **Render** | Free forever | ⭐⭐⭐⭐ | Yes (spins down after 15 min) | Yes |
| **Azure App Service** | Free with student credits | ⭐⭐⭐ | No | Yes |

---

### Option A — Railway (Recommended, Easiest)

[Railway](https://railway.app) auto-detects Spring Boot projects and builds via Dockerfile or Nixpacks. **Best DX (Developer Experience).**

#### Step-by-step:

1. **Go to** [railway.app](https://railway.app) and sign in with **GitHub**

2. **Click** "New Project" → "Deploy from GitHub repo"

3. **Select** your `LearnCodeAdventure-SpringBoot` repository

4. **Railway auto-detects** the Dockerfile and starts building. Wait 2–4 minutes.

5. **Add environment variable** (if not auto-detected):
   - Go to your project → **Variables** tab
   - Click "New Variable"
   - Add:
     | Variable | Value |
     |----------|-------|
     | `SPRING_PROFILES_ACTIVE` | `prod` |
     | `PORT` | `8080` |

6. **Generate a public URL**:
   - Go to **Settings** tab
   - Under **Networking** → click "Generate Domain"
   - You'll get a URL like: `https://learncode-adventure-production.up.railway.app`

7. **Open your URL** — your app is live! 🎉

#### Automatic redeployment:
Every time you `git push` to `main`, Railway automatically rebuilds and redeploys.

---

### Option B — Render (Free Tier)

[Render](https://render.com) offers a permanent free tier for web services.

#### Step-by-step:

1. **Go to** [render.com](https://render.com) and sign in with **GitHub**

2. **Click** "New" → "Web Service"

3. **Connect** your `LearnCodeAdventure-SpringBoot` repository

4. **Configure** the service:

   | Setting | Value |
   |---------|-------|
   | **Name** | `learncode-adventure` |
   | **Region** | Choose closest to your users |
   | **Runtime** | `Docker` |
   | **Instance Type** | `Free` |

5. **Add environment variables** — click "Advanced" → "Add Environment Variable":

   | Key | Value |
   |-----|-------|
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `PORT` | `8080` |

6. **Click** "Create Web Service"

7. **Wait** 3–5 minutes for the build to complete

8. **Your URL** will be: `https://learncode-adventure.onrender.com`

> ⚠️ **Free tier limitation:** The app **spins down after 15 minutes of inactivity**. The first request after sleep takes ~30 seconds to wake up.

---

### Option C — Azure App Service (Student Credits)

If you have **Azure for Students** ($100 free credits), this gives you a production-grade deployment.

#### Step-by-step:

1. **Install Azure CLI:**
   ```bash
   # Windows (winget)
   winget install Microsoft.AzureCLI

   # macOS (Homebrew)
   brew install azure-cli
   ```

2. **Login to Azure:**
   ```bash
   az login
   ```

3. **Create the web app and deploy in one command:**
   ```bash
   cd LearnCodeAdventure-SpringBoot

   az webapp up \
     --name learncode-adventure \
     --resource-group LearnCodeRG \
     --runtime "JAVA:21-java21" \
     --sku F1 \
     --location westeurope
   ```

   > `--sku F1` = Free tier. `--location westeurope` = closest to Morocco. Change as needed.

4. **Set the production profile:**
   ```bash
   az webapp config appsettings set \
     --name learncode-adventure \
     --resource-group LearnCodeRG \
     --settings SPRING_PROFILES_ACTIVE=prod
   ```

5. **Your URL:** `https://learncode-adventure.azurewebsites.net`

#### For future redeployments:
```bash
az webapp up --name learncode-adventure
```

---

## ⚙️ Production Configuration Reference

### Environment Variables

| Variable | Purpose | Default |
|----------|---------|---------|
| `PORT` | Server port (set by hosting platform) | `8080` |
| `SPRING_PROFILES_ACTIVE` | Activate production config | `default` (dev) |

### Build the JAR manually

```bash
# Build (skip tests for faster build)
./mvnw clean package -DskipTests

# Run the JAR
java -jar -Dspring.profiles.active=prod target/LearnCodeAdventure-1.0.0.jar
```

### Docker commands (local testing)

```bash
# Build the Docker image
docker build -t learncode-adventure .

# Run the container
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod learncode-adventure

# Open: http://localhost:8080
```

---

## 🔧 Troubleshooting

| Problem | Solution |
|---------|----------|
| **Port 8080 already in use** | Change `server.port` in `application.properties` or set `PORT` env variable |
| **`Cannot resolve symbol 'SpringApplication'`** | Maven dependencies not loaded → Reload Maven (IntelliJ: Maven tab → ↺) |
| **`UnsupportedClassVersionError`** | Wrong Java version → install Java 21+ |
| **`No suitable driver found for jdbc:h2`** | Maven didn't download H2 → check internet, reload Maven |
| **Blank page in browser** | Clear browser cache (`Ctrl+Shift+R`) or try another browser |
| **Build fails on Railway/Render** | Check that `mvnw`, `.mvn/` folder, and `Dockerfile` are committed to Git |
| **App crashes on deploy — `PORT` issue** | Make sure `application-prod.properties` has `server.port=${PORT:8080}` |
| **Data resets on every redeploy** | Normal with in-memory H2. Demo data is re-inserted automatically. |

---

## 🎮 The 3 Games

### 🧩 Puzzle Python
Drag-and-drop code lines into the correct order.
- +10 pts per correct answer, −1 life per mistake
- 6 levels: Beginner → Advanced

### 🏃 Runner Quiz
Run, jump over obstacles (Space), collect stars (+2 pts each).
- Enter a door → Python quiz question (30 seconds)
- +20 pts correct, −1 life incorrect

### 🧠 Code Memory
Flip cards to match Python keywords with their definitions.
- +5 pts per pair + time bonus

---

## 📜 License

This project was created for educational purposes.

---

<p align="center">
  Made with ❤️ for learning Python through play
</p>
