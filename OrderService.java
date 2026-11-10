import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderService {

    public boolean placeOrder(int productId, int quantity) {
        String checkStockQuery = "SELECT price, stock_quantity FROM products WHERE product_id = ?";
        String updateStockQuery = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
        String insertOrderQuery = "INSERT INTO orders (total_amount) VALUES (?)";
        String insertOrderItemQuery = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Transaction Başlat
            conn.setAutoCommit(false);

            try (PreparedStatement checkStmt = conn.prepareStatement(checkStockQuery)) {
                checkStmt.setInt(1, productId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Hata: Ürün bulunamadı!");
                    conn.rollback();
                    return false;
                }

                double price = rs.getDouble("price");
                int currentStock = rs.getInt("stock_quantity");

                if (currentStock < quantity) {
                    System.out.println("Hata: Yetersiz stok! Mevcut: " + currentStock);
                    conn.rollback();
                    return false;
                }

                // 1. Stok Düşür
                try (PreparedStatement updateStmt = conn.prepareStatement(updateStockQuery)) {
                    updateStmt.setInt(1, quantity);
                    updateStmt.setInt(2, productId);
                    updateStmt.executeUpdate();
                }

                // 2. Sipariş Kaydı Aç
                int orderId = 0;
                double total = price * quantity;
                try (PreparedStatement orderStmt = conn.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                    orderStmt.setDouble(1, total);
                    orderStmt.executeUpdate();
                    ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    }
                }

                // 3. Sipariş Detayını Ekle
                try (PreparedStatement itemStmt = conn.prepareStatement(insertOrderItemQuery)) {
                    itemStmt.setInt(1, orderId);
                    itemStmt.setInt(2, productId);
                    itemStmt.setInt(3, quantity);
                    itemStmt.setDouble(4, price);
                    itemStmt.executeUpdate();
                }

                // Her şey başarılıysa onayla
                conn.commit();
                System.out.println("Sipariş başarıyla oluşturuldu! Sipariş ID: " + orderId);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.out.println("İşlem hatası, değişiklikler geri alındı (Rollback): " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
