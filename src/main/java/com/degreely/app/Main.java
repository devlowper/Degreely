package com.degreely.app;

import com.degreely.app.controller.UniversityService;
import com.degreely.app.model.Scholarship;
import com.degreely.app.model.University;
import com.degreely.app.view.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Setup modern FlatLaf Look and Feel
        FlatLightLaf.setup();

        // Test Data Layer loading
        System.out.println("==================================================");
        System.out.println("            Degreely - Data Layer Test            ");
        System.out.println("==================================================");
        UniversityService service = UniversityService.getInstance();
        List<University> universities = service.getAll();
        List<Scholarship> scholarships = service.getAllScholarships();

        System.out.println("-> Successfully loaded " + universities.size() + " universities from JSON.");
        if (!universities.isEmpty()) {
            University u = universities.get(0);
            System.out.println("   Sample Uni: " + u.getName() + " (#" + u.getRanking() + ", " + u.getCountry() + ")");
            System.out.println("   Programs count: " + u.getPrograms().size());
        }

        System.out.println("-> Successfully loaded " + scholarships.size() + " scholarships from JSON.");
        if (!scholarships.isEmpty()) {
            Scholarship s = scholarships.get(0);
            System.out.println("   Sample Scholarship: " + s.getName() + " (" + s.getAmount() + ")");
        }

        // Test search method
        List<University> searchResult = service.search("Computer", "United States", "Bachelor");
        System.out.println("-> Test Search ('Computer', 'United States', 'Bachelor'): Found " + searchResult.size() + " matches.");
        System.out.println("==================================================");

        // Launch MainFrame on the Swing Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
