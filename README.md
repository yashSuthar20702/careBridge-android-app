# 🏥 CareBridge — Smart Patient Care & Medication Management App for PSWs

> A comprehensive Android solution to digitize patient care and medication management for **Personal Support Workers (PSWs)**, **patients**, and **doctors**.

---

## 📘 Overview

**CareBridge** is a modern healthcare management system designed to simplify the coordination between **doctors**, **patients**, and **caregivers (PSWs)**.  
It ensures patients take their medicines on time, while caregivers and doctors can monitor adherence, health trends, and performance through a unified platform.

---

## 🎯 Objectives

- 📱 Digitize doctor–patient–caregiver coordination  
- 💊 Ensure timely medicine intake with automated reminders  
- 🧑‍⚕️ Help caregivers monitor multiple patients easily  
- 📈 Generate monthly reports for health tracking and adherence insights  

---

## ⚠️ Scope Clarification

> **Note:** AI-based meal planning and meal management modules are **not included** in this app version.  
> This repository focuses on **medicine management**, **notifications**, and **health tracking** only.

---

## 🚨 Problem Statement

- Patients (especially elderly or chronically ill) often **forget to take medicines** on time  
- Caregivers struggle to manage **multiple patients simultaneously**  
- Doctors lack **real-time insight** into patient adherence and progress  
- Existing solutions are **either incomplete or not user-friendly**

---

## 💡 Proposed Solution

- Develop a **role-based Android app** for doctors, caregivers, and patients  
- **Doctors** can assign caregivers and upload prescriptions  
- **Caregivers** track medicine adherence and patient health  
- **Patients** receive real-time **push notifications** for upcoming and missed medicines  
- Use **Firebase Cloud Messaging (FCM)** and **AlarmManager** for reliable notification delivery  

---

## 🧩 Key Features

### 🔐 Authentication Module
- Secure login for Doctors, Caregivers, and Patients  
- Role-based access control with personalized dashboards  

### 💊 Medicine Management
- Prescription uploads and reminders  
- Patient acknowledgment: **Taken / Not Taken**  
- Caregiver override and adherence logging  

### ❤️ Health Tracking
- Daily physical & mental health surveys  
- Trend analysis for monthly reporting  

### 📊 Reporting Module
- Auto-generated PDF/CSV reports  
- Displays medicine adherence & health progress  

### 🔔 Notifications & Reminders
- **AlarmManager** for offline local reminders  
- **Firebase Cloud Messaging (FCM)** for real-time push alerts  
  - Upcoming medicine alerts  
  - Missed medicine or caregiver notifications  

---

## 👥 User Roles

| Role | Description |
|------|--------------|
| 👨‍⚕️ **Doctor** | Creates accounts, assigns caregivers, uploads prescriptions, monitors reports |
| 🧑‍🦽 **Patient** | Views schedule, receives reminders, completes daily health surveys |
| 🧑‍⚕️ **Caregiver (PSW)** | Manages multiple patients, updates adherence logs, reviews health data |

---

## 🧱 System Modules

1. **Authentication Module** — Role-based login and management  
2. **Medicine Module** — Prescriptions, reminders, and logs  
3. **Survey Module** — Daily patient wellness reports  
4. **Reporting Module** — Monthly reports in PDF/CSV  
5. **Notification Module** — Push (FCM) + Local (AlarmManager) alerts  

---

## 🗄️ Database Schema (MySQL)

| Table | Description |
|--------|-------------|
| **Users** | Stores user details (id, name, role, doctor_id) |
| **Patients** | Contains patient health info and caregiver link |
| **Prescriptions** | Holds prescription details (medicine name, dosage, time) |
| **MedicineLog** | Tracks taken/missed status and timestamps |
| **Surveys** | Stores daily patient health survey responses |

---

## ☁️ Firebase Cloud Messaging (FCM)

### 🔧 How It Works
1. Each Android device registers and receives a **unique FCM token**.  
2. Token is sent to your backend (**MySQL + PHP API**).  
3. Backend uses **FCM REST API** to send push notifications:  
   - Medicine reminders  
   - Real-time caregiver updates  

### 📄 Implementation Highlights
- `MyFirebaseMessagingService.java`  
  - Handles token refresh (`onNewToken`)  
  - Processes messages (`onMessageReceived`)  
  - Builds custom notifications with `NotificationCompat`  

```java
@Override
public void onMessageReceived(RemoteMessage remoteMessage) {
    if (remoteMessage.getNotification() != null) {
        sendNotification(remoteMessage.getNotification().getTitle(),
                         remoteMessage.getNotification().getBody());
    }
}
