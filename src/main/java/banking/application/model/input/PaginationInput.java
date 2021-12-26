package banking.application.model.input;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Input model class for pagination
 */
public class PaginationInput {
    @PositiveOrZero(message = "pagination.offset must be ≥ 0")
    int offset;
    @Positive(message = "pagination.limit must be > 0")
    int limit;

    public PaginationInput(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
