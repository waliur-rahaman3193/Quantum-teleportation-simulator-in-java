import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

public class TeleportationSimulator {

    // ==== Inner class for Qubit ====
    static class Qubit {
        double[] real = new double[2];
        double[] imag = new double[2];

        public Qubit(double r0, double i0, double r1, double i1) {
            real[0] = r0;
            imag[0] = i0;
            real[1] = r1;
            imag[1] = i1;
        }

        public String getState() {
            return "|0>: " + real[0] + "+" + imag[0] + "i, |1>: " + real[1] + "+" + imag[1] + "i";
        }
    }

    // ==== Quantum operations ====
    static void hadamard(Qubit q) {
        double[] r = new double[2];
        double[] i = new double[2];
        r[0] = (q.real[0] + q.real[1]) / Math.sqrt(2);
        i[0] = (q.imag[0] + q.imag[1]) / Math.sqrt(2);
        r[1] = (q.real[0] - q.real[1]) / Math.sqrt(2);
        i[1] = (q.imag[0] - q.imag[1]) / Math.sqrt(2);
        q.real = r;
        q.imag = i;
    }

    static void cnot(Qubit control, Qubit target) {
        double tempR = target.real[0];
        double tempI = target.imag[0];
        if (Math.abs(control.real[1]) > 0.1 || Math.abs(control.imag[1]) > 0.1) {
            target.real[0] = target.real[1];
            target.imag[0] = target.imag[1];
            target.real[1] = tempR;
            target.imag[1] = tempI;
        }
    }

    static void applyX(Qubit q) {
        double tempR = q.real[0];
        double tempI = q.imag[0];
        q.real[0] = q.real[1];
        q.imag[0] = q.imag[1];
        q.real[1] = tempR;
        q.imag[1] = tempI;
    }

    static void applyZ(Qubit q) {
        q.real[1] = -q.real[1];
        q.imag[1] = -q.imag[1];
    }

    // ==== Classical Data Structures ====
    static ArrayList<String> history = new ArrayList<>();
    static Stack<String> undoStack = new Stack<>();
    static Queue<String> requestQueue = new LinkedList<>();

    static class SavedQubit {
        String name;
        Qubit qubit;

        public SavedQubit(String name, Qubit qubit) {
            this.name = name;
            this.qubit = qubit;
        }
    }

    static ArrayList<SavedQubit> savedQubits = new ArrayList<>();

    // ==== Teleportation Simulation ====
    static String teleport(Qubit original, Qubit entangledA, Qubit entangledB) {
        hadamard(entangledA);
        cnot(entangledA, entangledB);
        cnot(original, entangledA);
        hadamard(original);

        int m1 = (int)(Math.random() * 2);
        int m2 = (int)(Math.random() * 2);

        if (m2 == 1) applyX(entangledB);
        if (m1 == 1) applyZ(entangledB);

        String result = "Measurements: m1=" + m1 + ", m2=" + m2 + "\n" +
                "Teleported state: " + entangledB.getState();

        history.add(result);
        undoStack.push(result);

        return result;
    }

    static void sortHistory(ArrayList<String> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                if (list.get(j).compareTo(list.get(j + 1)) > 0) {
                    String temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }

    // ==== Main GUI ====
    public static void main(String[] args) {
        JFrame frame = new JFrame("Quantum Teleportation Simulator - With Algorithms");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea output = new JTextArea();
        output.setEditable(false);
        JScrollPane scroll = new JScrollPane(output);

        JButton simulateBtn = new JButton("Start Teleportation");
        JButton showHistoryBtn = new JButton("Show History");
        JButton undoBtn = new JButton("Undo Last");
        JButton sortBtn = new JButton("Sort History");
        JButton saveStateBtn = new JButton("Save Qubit State");
        JButton viewQueueBtn = new JButton("View Request Queue");
        JButton viewSavedBtn = new JButton("View Saved States");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        panel.add(simulateBtn);
        panel.add(showHistoryBtn);
        panel.add(undoBtn);
        panel.add(sortBtn);
        panel.add(saveStateBtn);
        panel.add(viewQueueBtn);
        panel.add(viewSavedBtn);

        // ✅ Modified Button with User Input
        simulateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double r0 = Double.parseDouble(JOptionPane.showInputDialog("Enter Re(|0>):"));
                    double i0 = Double.parseDouble(JOptionPane.showInputDialog("Enter Im(|0>):"));
                    double r1 = Double.parseDouble(JOptionPane.showInputDialog("Enter Re(|1>):"));
                    double i1 = Double.parseDouble(JOptionPane.showInputDialog("Enter Im(|1>):"));

                    Qubit original = new Qubit(r0, i0, r1, i1);
                    Qubit entangledA = new Qubit(1.0, 0.0, 0.0, 0.0);
                    Qubit entangledB = new Qubit(1.0, 0.0, 0.0, 0.0);

                    String result = teleport(original, entangledA, entangledB);
                    output.setText(result + "\n");

                    requestQueue.offer("Teleportation Request at " + new Date());
                } catch (Exception ex) {
                    output.setText("Invalid input. Please enter valid numbers.");
                }
            }
        });

        showHistoryBtn.addActionListener(e -> {
            output.setText("--- Teleportation History ---\n");
            for (String record : history) {
                output.append(record + "\n\n");
            }
        });

        undoBtn.addActionListener(e -> {
            if (!undoStack.isEmpty()) {
                output.setText("Undoing: \n" + undoStack.pop());
            } else {
                output.setText("No teleportation to undo.\n");
            }
        });

        sortBtn.addActionListener(e -> {
            sortHistory(history);
            output.setText("Sorted history by measurements.\n");
        });

        saveStateBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter a name to save this state:");
            if (name != null && !name.trim().isEmpty()) {
                Qubit saved = new Qubit(1.0, 0.0, 0.0, 0.0);
                savedQubits.add(new SavedQubit(name, saved));
                output.setText("Qubit saved as '" + name + "'.\n");
            }
        });

        viewQueueBtn.addActionListener(e -> {
            output.setText("--- Request Queue ---\n");
            for (String req : requestQueue) {
                output.append(req + "\n");
            }
        });

        viewSavedBtn.addActionListener(e -> {
            output.setText("--- Saved Qubit States ---\n");
            for (SavedQubit sq : savedQubits) {
                output.append("Name: " + sq.name + ", State: " + sq.qubit.getState() + "\n");
            }
        });

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
