import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportService {

    public void printTopSellingProducts() {
        String query = """
            SELECT p.name, SUM(oi.quantity) AS total_sold, SUM(oi.quantity * oi.unit_price) AS total_revenue
            FROM order_items oi
            JOIN products p ON oi.product_id = p.product_id
            GROUP BY p.product_id, p.name
            ORDER BY total_sold DESC;
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n--- EN ÇOK SATAN ÜRÜNLER RAPORU ---");
            while (rs.next()) {
                System.out.printf("Ürün: %-20s | Satış Adedi: %-5d | Toplam Ciro: $%.2f%n",
                        rs.getString("name"),
                        rs.getInt("total_sold"),
                        rs.getDouble("total_revenue"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
