package banking.application.model.input;

import banking.application.util.TransactionType;
import org.springframework.lang.Nullable;

import java.util.Date;

public class TransactionPageInput {
    PaginationInput pagination;
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
    TransactionType transactionType;
}

