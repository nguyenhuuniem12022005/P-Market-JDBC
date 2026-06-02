package view.report;

import view.user.UiHelper;

import dao.ReportDAO;
import model.Report;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Module h - Duyệt báo cáo (danh sách) - ReportManageFrm */
public class ReportManageFrm extends JFrame implements ActionListener {

    private final ReportDAO reportDAO = new ReportDAO();
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<String> inStatus = new JComboBox<>(new String[] {
            "Chờ xử lý", "Đã xử lý", "Bị bác bỏ", "Tất cả"
    });
    private final JButton btnDetail = new JButton("Xem chi tiết");
    private List<Report> reports = List.of();

    public ReportManageFrm() {
        super("Quản lý báo cáo");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(
                new String[] { "ID", "Người gửi", "Loại", "Đối tượng ID", "Lý do", "Trạng thái" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedReport();
                }
            }
        });

        btnDetail.addActionListener(this);
        inStatus.addActionListener(this);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Trạng thái:"));
        top.add(inStatus);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(btnDetail);
        bottom.add(new JLabel("Chọn dòng báo cáo rồi bấm Xem chi tiết"));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        loadTable();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnDetail) {
            openSelectedReport();
        } else if (e.getSource() == inStatus) {
            loadTable();
        }
    }

    // Load danh sách báo cáo 
    private void loadTable() {
        try {
            reports = switch (inStatus.getSelectedIndex()) {
                case 1 -> reportDAO.getReportsByStatus(Report.STATUS_RESOLVED);
                case 2 -> reportDAO.getReportsByStatus(Report.STATUS_REJECTED);
                case 3 -> reportDAO.getAllReports();
                default -> reportDAO.getPendingReports();
            };
            model.setRowCount(0);
            for (Report r : reports) {
                model.addRow(new Object[] {
                        r.getId(),
                        r.getReporter() != null ? r.getReporter().getFullName() : "",
                        r.getPostId() != null ? "Bài đăng" : "Tài khoản",
                        r.getPostId() != null ? r.getPostId() : r.getAccountId(),
                        r.getReason(),
                        UiHelper.statusLabel(r.getStatus())
                });
            }
        } catch (Exception ex) {
            UiHelper.showError(this, ex.getMessage());
        }
    }

    // Mở báo cáo được chọn
    private void openSelectedReport() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= reports.size()) {
            UiHelper.showError(this, "Vui lòng chọn một báo cáo.");
            return;
        }
        new ReportDetailFrm(reports.get(row), this::loadTable).setVisible(true);
    }
}
