# 🚀 StartupGuard

**StartupGuard** is a lightweight observability library for **Spring Boot applications**.  
It monitors application startup time, tracks bean initialization performance, and detects performance regressions (slowness) by comparing current runs against a historical baseline.

It helps developers and CI/CD pipelines identify **heavy dependencies** or **code changes** that silently increase startup time.

---

## ✨ Features

- **Startup Time Monitoring**  
  Captures total startup time, heap memory usage, and loaded class count.

- **Regression Detection**  
  Compares current metrics against a stored baseline. If startup is significantly slower, it flags it as a regression.

- **Bean Initialization Tracking**  
  Identifies which specific Spring Beans are taking the longest to initialize.

- **Smart Hardware Check**  
  Detects environment changes (e.g., different CPU cores or memory) and automatically resets the baseline to avoid false positives.

- **HTML Reporting**  
  Generates a visual `startup-report.html` file summarizing the health of your startup.

- **GC Inspection**  
  Optionally logs Garbage Collection details to help diagnose memory-related delays.

---

## 📦 Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.vedant</groupId>
    <artifactId>startupguard</artifactId>
    <version>1.0.0</version>
</dependency>
```
---
## ✅ Requirements

- **Java 21**
- **Spring Boot 3.x / 4.x**

---

## ⚙️ Configuration Properties

StartupGuard is **disabled by default**.  
You must explicitly enable it in `application.properties` or `application.yml`.

| Property | Type | Default | Description |
|--------|------|---------|-------------|
| `startup-guard.enabled` | boolean | `false` | **Master Switch**. Set to `true` to activate StartupGuard. If `false`, the library does nothing and has zero overhead. |
| `startup-guard.threshold-percent` | double | `5.0` | **Sensitivity Level**. Allowed percentage increase in startup time before it is considered slow. <br>Example: Baseline = `1000ms`, threshold = `5.0` → flagged if startup > `1050ms`. |
| `startup-guard.baseline-path` | String | `startup-baseline.json` | **Storage File**. File path where metrics (time, memory, bean stats) are saved. Automatically created/updated. |
| `startup-guard.smart-hardware-check` | boolean | `false` | **Environment Drift Protection**. Resets baseline if CPU cores or memory change significantly. Useful across different developer machines. |
| `startup-guard.focus-package` | String | `null` | **Log Filter**. If set (e.g. `com.mycompany`), only beans from this package are shown in *Heaviest Components*. |
| `startup-guard.generate-html` | boolean | `false` | **Visual Report**. Generates `startup-report.html` after every startup. |
| `startup-guard.inspect-gc` | boolean | `false` | **GC Debugging**. Logs Garbage Collection statistics to the console. |

---

## 🧪 Example Configuration (`application.properties`)

```properties
startup-guard.enabled=true
startup-guard.threshold-percent=10.0
startup-guard.smart-hardware-check=true
startup-guard.generate-html=true
startup-guard.focus-package=com.vedant.myapp
```
---
## 🛠 How To Use

### 1️⃣ First Run (Baseline Creation)

Start your application.  
StartupGuard detects that no baseline exists and saves the current metrics to `startup-baseline.json`.

**Log:**
```🆕 First run! Establishing baseline.```

---
### 2️⃣ Standard Run (Monitoring)
Restart your application. StartupGuard compares the new startup time against the baseline.
```
✅ Healthy Startup
Startup Healthy: 3400ms
🏆 New Personal Best
If startup is faster than before, the baseline is updated automatically.
🚨 Regression Detected
If startup is slower than the configured threshold:
SLOW STARTUP: +500ms slower than baseline
```
---

### 3️⃣ Analyze Slowness
```
Check the logs or open `startup-report.html`.  
StartupGuard pinpoints the exact causes:

[UserService] took 150ms (Slower by 50ms)
[NewHeavyBean] took 200ms (New Bean)
```
---

## 📜 Sample Logs (Real Output)

```text
2026-01-04T14:04:12.359+05:30  INFO  --- Starting MyAppApplication using Java 21.0.7
2026-01-04T14:04:16.746+05:30  INFO  --- Started MyAppApplication in 4.969 seconds
--- 🗑️ GC Inspector ---
[G1 Young Generation] ran 11 times (137ms)
[G1 Concurrent GC] ran 4 times (4ms)
-----------------------
--- 🐢 Heaviest Components ---
[slowDown_v [com.test.my_app.SlowDown_v]] took 3227ms
------------------------------
🚨 SLOW STARTUP: +4327ms slower than baseline.
📄 HTML Report generated: startup-report.html
```
---

## 🎯 Ideal Use Cases

- Catch startup regressions in **CI/CD pipelines**
- Identify **slow or newly added beans**
- Monitor startup health in **large Spring Boot projects**
- Prevent unnoticed performance degradation over time

---

## ✨ Final Note

**StartupGuard** ensures your Spring Boot application stays **fast, predictable, and regression-free** ⚡  

Crafted with ❤️ by **Vedant Singh**.  
This library is **open for everyone** — feel free to use it in personal projects, enterprise systems, or CI pipelines.

> ⚠️ Just one thing to remember:  
> **StartupGuard is disabled by default — don’t forget to enable it!**

💡 Have ideas, improvements, or feature requests?  
Suggestions and contributions are **always welcome**. Let’s make Spring Boot startups faster together 🚀

