# 🗓️ Time Table Builder - Java Swing Project

## 📚 Project Description

**Time Table Builder** is a Java-based GUI application developed using **Swing**. It provides **role-based dashboards** for Admin, Teacher, and Student users. The system aims to automate timetable generation, conflict-free schedule visualization, and enhance user access to course information through a friendly GUI.

The project reads input from CSV files, processes user login and course data, and generates dashboards accordingly.

---

## 🧾 Features Overview

- 🔐 **Login System**
  - Role selection via ComboBox: `Admin`, `Teacher`, `Student`
  - Login validation using CSV files:
    - `teachers.csv` and `students.csv` store credentials
    - Admin credentials are hardcoded

- 👨‍🏫 **Teacher Dashboard**
  - Displays classes taught by the logged-in teacher
  - Shows course name, room number, time slot, and days

- 🧑‍🎓 **Student Dashboard**
  - Shows enrolled courses for the student
  - Presents similar info as teacher dashboard, customized per student

- 🧩 **Admin Dashboard**
  - Currently a placeholder for future features like timetable editing

---

## 🧱 Class-wise Breakdown

### 🔹 `LoginPage.java`
- Displays the login GUI
- Provides role selection (ComboBox)
- Reads credentials from `teachers.csv` or `students.csv`
- Opens respective dashboard upon successful login

### 🔹 `SignupPage.java`
- Placeholder for future user sign-up functionality

### 🔹 `TeacherDashboard.java`
- Accepts `teacherID` from login
- Filters course data from `admin_courses.csv`
- Populates a `JTable` with class info relevant to the teacher
- Utilizes the `ClassInfo` model

### 🔹 `StudentDashboard.java`
- Similar to `TeacherDashboard`, but filters by student enrollment
- Uses both `students.csv` and `admin_courses.csv` to map student-course data

### 🔹 `AdminDashboard.java`
- Placeholder for admin-specific features
- Can be expanded to allow timetable creation or CSV updates

---

### 🔸 `ClassInfo.java`
- **Model class** to represent a course entry
- Fields:
  - `code`: course code (e.g., CS101)
  - `instructor`: instructor ID/name
  - `days`: string or list of teaching days
  - `roomNo`: class location
  - `timeSlot`: string like `10:00-11:00`
- Provides:
  - Constructors for different use-cases
  - Getter methods like `getDays()`, `getCourse()`

### 🔸 `Course.java`
- [**Optional/Planned**] For advanced implementations:
  - May represent additional course metadata like credits, department

### 🔸 `Classroom.java`
- [**Optional/Planned**] May represent classroom capacities, locations, and features
- Can be used to avoid scheduling conflicts during auto-generation

---

## 📁 CSV Files Structure

All CSV files are located inside the `users/` directory.

### ✅ `teachers.csv`
| userid   | password |
|----------|----------|
| alice123 | pass123  |

### ✅ `students.csv`
| userid   | password |
|----------|----------|
| s2021010 | 123456   |

### ✅ `admin_courses.csv`
| course_code | instructor | days       | timeSlot      | room  |
|-------------|------------|------------|---------------|-------|
| CS101       | alice123   | Mon Wed    | 10:00-11:00   | R101  |
| MA201       | bob456     | Tue Thu    | 11:00-12:00   | R202  |

---

## 🎮 How to Run the Project

1. Open the project in **Eclipse IDE**
2. Place CSV files (`teachers.csv`, `students.csv`, `admin_courses.csv`) inside the `users/` directory
3-a).Compile the program it with this command "javac -cp ".;lib\jbcrypt-0.4.jar" SignupPage.java LoginPage.java AdminDashboard.java StudentDashboard.java TeacherDashboard.java"
3-b). Then run this command "java -cp ".;lib\jbcrypt-0.4.jar" LoginPage"
4. Choose your login role from the ComboBox
5. Enter correct credentials:
   - For Admin: use hardcoded credentials
	(the credential to login Admin : USERNAME : admin ; PASSWORD : 123
   - For Teacher/Student: enter manually
6. On successful login, the appropriate dashboard opens

---

## 🧪 Sample Flow

**Example 1: Teacher Login**
- Teacher `alice123` logs in
- `TeacherDashboard` displays all entries from `admin_courses.csv` where instructor is `alice123`

**Example 2: Student Login**
- Student `s2021010` logs in
- `StudentDashboard` shows enrolled courses (assumes student-course mapping logic is in place or to be implemented)

---

## 🛠 Tools & Technologies

- 💻 Java (JDK 8+)
- 🎨 Java Swing (JTable, JFrame, JComboBox, etc.)
- 📦 Eclipse IDE
- 📄 CSV File Handling (`BufferedReader`, `FileReader`)
- 🔧 External Library (e.g., `jbcrypt-0.4.jar`) — if password hashing is added

---

## 🌱 Future Enhancements

- [ ] Implement full **AdminDashboard** with editing tools
- [ ] Support **sign-up** and new user creation
- [ ] Add **conflict resolution** when adding new courses
- [ ] Use **FileChooser** to allow flexible CSV path selection
- [ ] Support **student enrollment mapping** to courses via new CSV
- [ ] Export timetables to PDF or Excel


