package dao;

import model.ReportEvidence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportEvidenceDAO extends DAO {

    /** Module g: luu danh sach anh bang chung cho mot bao cao */
    public boolean addEvidenceList(int reportId, List<String> imageUrls) throws SQLException {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return true;
        }
        String sql = "INSERT INTO tblReportEvidence (reportId, imageUrl) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (String url : imageUrls) {
                ps.setInt(1, reportId);
                ps.setString(2, url);
                ps.addBatch();
            }
            ps.executeBatch();
        }
        return true;
    }

    /** Module h: lay danh sach anh bang chung theo bao cao */
    public List<ReportEvidence> getEvidenceByReportId(int reportId) throws SQLException {
        String sql = "SELECT * FROM tblReportEvidence WHERE reportId=?";
        List<ReportEvidence> list = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReportEvidence e = new ReportEvidence();
                    e.setId(rs.getInt("id"));
                    e.setImageUrl(rs.getString("imageUrl"));
                    list.add(e);
                }
            }
        }
        return list;
    }
}
