package banking.application.model.output;

import banking.application.model.input.PaginationInput;

/**
 * Model for return data for pagination
 */
public class PaginationOutput extends PaginationInput {
    long count;

    public PaginationOutput(int offset, int limit, long count) {
        super(offset, limit);
        this.count = count;
    }

    public long getCount() {
        return count;
    }
}
