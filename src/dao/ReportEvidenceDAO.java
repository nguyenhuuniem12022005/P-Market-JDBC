package dao;

import model.ReportEvidence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportEvidenceDAO extends DAO {

    public void insertEvidence(int reportId, String imageUrl) throws SQLException {
        String sql = "INSERT INTO tblReportEvidence (reportId, imageUrl) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            ps.setString(2, imageUrl);
            ps.executeUpdate();
        }
    }

    public List<ReportEvidence> getByReportId(int reportId) throws SQLException {
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
