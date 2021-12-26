package banking.application.model.input;

import banking.application.util.TransactionType;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Model for transaction page input
 */
public class TransactionPageInput {
    @NotNull(message = "pagination is missing")
    @Valid PaginationInput pagination;
    @NotNull(message = "ioFilter filter is missing")
    IOFilter ioFilter;

    @Nullable
    Date startDate;
    @Nullable
    Date endDate;

    @Nullable
    String recipientName;
    @Nullable
    String recipientIban;

    @Nullable
    Double amountFrom;
    @Nullable
    Double amountTo;

    @Nullable
    String currency;

    @Nullable
    String title;

    @Nullable
    SortType sortType;

    // Null - all types
    @Nullable
    List<TransactionType> transactionType;

    public TransactionPageInput(PaginationInput pagination, IOFilter ioFilter, @Nullable Date startDate, @Nullable Date endDate, @Nullable String recipientName, @Nullable String recipientIban, @Nullable Double amountFrom, @Nullable Double amountTo, @Nullable List<TransactionType> transactionType) {
        this.pagination = pagination;
        this.ioFilter = ioFilter;
        this.startDate = startDate;
        this.endDate = endDate;
        this.recipientName = recipientName;
        this.recipientIban = recipientIban;
        this.amountFrom = amountFrom;
        this.amountTo = amountTo;
        this.transactionType = transactionType;
    }

    public PaginationInput getPagination() {
        return pagination;
    }

    public IOFilter getIoFilter() {
        return ioFilter;
    }

    @Nullable
    public Date getStartDate() {
        return startDate;
    }

    @Nullable
    public Date getEndDate() {
        return endDate;
    }

    @Nullable
    public String getRecipientName() {
        return recipientName;
    }

    @Nullable
    public String getRecipientIban() {
        return recipientIban;
    }

    @Nullable
    public Double getAmountFrom() {
        return amountFrom;
    }

    @Nullable
    public Double getAmountTo() {
        return amountTo;
    }

    @Nullable
    public List<TransactionType> getTransactionType() {
        return transactionType;
    }

    @Nullable
    public String getCurrency() {
        return currency;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public SortType getSortType() {
        return sortType;
    }
}

