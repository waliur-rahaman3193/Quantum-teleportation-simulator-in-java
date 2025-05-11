# 🧬 Quantum Teleportation Simulator in Java

A beginner-friendly educational simulator that visually demonstrates how **quantum teleportation** works using Java and classical logic structures. This project showcases a simplified teleportation protocol including quantum entanglement, measurement, and classical communication.

---

## 📽️ Overview

Quantum teleportation is a process of transmitting the quantum state of a qubit from one location to another using:

- **Quantum entanglement**
- **Classical communication**

This simulator is designed for educational purposes and visualizes each step using a graphical interface.

---

## 📌 Features

- ✅ GUI created with **Java Swing**
- ✅ Input real & imaginary parts of qubit amplitudes
- ✅ Step-by-step simulation of the quantum teleportation protocol
- ✅ Uses `Stack`, `Queue`, and `ArrayList` for:
  - State history
  - Undo operations
  - Management of teleportation steps
- ✅ No unnecessary complexity — suitable for students and beginners

---

## 🧠 Teleportation Process

1. **Prepare** an unknown qubit |ψ⟩ = α|0⟩ + β|1⟩  
2. **Create** a Bell pair between Alice and Bob  
3. **Measure** qubit using Bell-state basis  
4. **Send** measurement result over classical channel  
5. **Apply** correction operations at Bob's end (Pauli X, Z gates)  
6. **Reconstruct** the original quantum state at Bob’s end  

---

## 📁 Project Structure

---

## 🛠️ How to Run

### ✅ Prerequisites:
- Java JDK 8 or higher
- IDE like IntelliJ or Eclipse (optional)

### 🔧 Compile & Run

```bash
git clone https://github.com/waliur-rahaman3193/QuantumTeleportationSimulator.git
cd QuantumTeleportationSimulator/src
javac *.java
java Main

