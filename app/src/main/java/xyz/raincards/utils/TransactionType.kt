package xyz.raincards.utils

enum class TransactionType(val code: Int, val description: String) {
    SALE(0x00, "Sale"),
    REFUND(0x20, "Refund"),
    SALE_WITH_CASHBACK(0x09, "Sale with Cashback"),
    PRE_AUTHORIZATION(0x01, "Pre-Authorization"),
    PRE_AUTH_COMPLETION(0x02, "Pre-Authorization Completion"),
    VOID(0x03, "Void"),
    BATCH_UPLOAD(0x04, "Batch Upload"),
    VOID_REFUND(0x21, "Void Refund"),
    BALANCE_INQUIRY(0x30, "Balance Inquiry"),
    PIN_CHANGE(0x31, "PIN Change"),
    TRANSFER(0x40, "Transfer"),
    DEPOSIT(0x41, "Deposit"),
    WITHDRAWAL(0x42, "Withdrawal"),
    PAYMENT(0x50, "Payment"),
    TOP_UP(0x60, "Top-Up"),
    BILL_PAYMENT(0x70, "Bill Payment"),
    QUASI_CASH(0x80, "Quasi-Cash"),
    ADMINISTRATIVE(0x90, "Administrative Transaction"),
    LOYALTY(0xA0, "Loyalty Transaction"),
    OFFLINE(0xB0, "Offline Transaction");

    companion object {
        fun fromCode(code: Int): TransactionType? {
            return entries.find { it.code == code }
        }

        fun isValidCode(code: Int): Boolean {
            return entries.any { it.code == code }
        }
    }
}
