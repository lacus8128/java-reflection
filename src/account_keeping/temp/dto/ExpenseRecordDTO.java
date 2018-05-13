/**
 *
 */
package account_keeping.temp.dto;

import java.time.LocalDateTime;

/**
 * 支出のデータ
 * （最初は日付の変数は使わないが、後の拡張を考えて実装しておく）
 */
public class ExpenseRecordDTO {

    private int id;
    private String place;
    private String product;
    private int amount;
    private String description;
    private LocalDateTime datetime;

    /* getter & setter */
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public String getProduct() {
        return product;
    }
    public void setProduct(String product) {
        this.product = product;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getDatetime() {
        return datetime;
    }
    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    /* デバッグ用 */
    @Override
    public String toString() {
        return "ExpenseRecordDTO {\"id\": " + id + ", \"place\": " + place + ", \"product\": " + product + ", \"amount\": " + amount
                + ", \"description\": " + description + ", \"datetime\": " + datetime + "}";
    }
}
