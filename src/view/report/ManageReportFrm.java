package view.report;

import view.user.UiHelper;

import dao.ReportDAO;
import model.Report;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Module h — Duyet bao cao (danh sach) */
public class ManageReportFrm extends JFrame {

    private final ReportDAO reportDAO = new ReportDAO();
    private List<Report> reports = List.of();

    public ManageReportFrm() {
        super("Quan ly bao cao");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nguoi gui", "Loai", "Doi tuong ID", "Ly do", "Trang thai"}, 0);
        JTable table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0 && row < reports.size()) {
                        new ReportDetailFrm(reports.get(row).getId(),
                                () -> loadTable(model)).setVisible(true);
                    }
                }
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(new JLabel("Double-click xem chi tiet va xu ly"), BorderLayout.SOUTH);
        loadTable(model);
    }

    private void loadTable(DefaultTableModel model) {
        try {
            reports = reportDAO.getPendingReports();
            model.setRowCount(0);
            for (Report r : reports) {
                model.addRow(new Object[]{
                        r.getId(),
                        r.getAccount() != null ? r.getAccount().getFullName() : "",
                        r.getTargetType(),
                        r.getTargetId(),
                        r.getReason(),
                        UiHelper.statusLabel(r.getStatus())
                });
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }
}
