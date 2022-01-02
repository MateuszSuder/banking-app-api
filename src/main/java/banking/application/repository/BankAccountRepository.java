package banking.application.repository;

import banking.application.model.AccountAbleToPay;
import banking.application.model.BankAccount;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends MongoRepository<BankAccount, String> {

    // Get account with codes only
    @Query(fields = "{'codes' : 1, '_id' : 0 }")
    BankAccount findItemById(String id);

    // Get account with currency only
    @Query(fields = "{'currencies' : 1, '_id' : 0 }")
    BankAccount findCurrencyById(String id);



    @Aggregation(pipeline = {
            "{ $match: { _id : '?0' }}",
            "{ $project: { _id: 0, lastLoan: { $last: '$loans' }}}",
            "{ $project: { lastInstallment: { $last: '$lastLoan.installments' }}}",
            "{ $project: { isActive : { $toBool: '$lastInstallment.amountLeftToPay' }}}"
    })
    Optional<Boolean> checkIfAccountIsActive(String iban);

    @Aggregation(pipeline = {
            "{ $match: { loans: { $exists: true }}}",
            "{ $unwind: { path: '$currencies' }}",
            "{ $match: { 'currencies.currency': 'PLN' }}",
            "{ $project: {" +
                    "  installments: {" +
                    "    $let: {" +
                    "      vars: {" +
                    "        lastLoan: { $last: '$loans' }" +
                    "      }," +
                    "      in: {" +
                    "        $filter: {" +
                    "          input: '$$lastLoan.installments'," +
                    "          as: 'installment'," +
                    "          cond: { $and: [" +
                    "            {$gte: ['$$NOW', '$$installment.paymentDay']}, " +
                    "            {$gt: ['$$installment.amountLeftToPay', 0]}" +
                    "          ]}" +
                    "        }" +
                    "      }" +
                    "    }" +
                    "  }," +
                    "  info: {$let: {" +
                    "    vars: {" +
                    "      lastLoan: { $last: '$loans' }" +
                    "    }," +
                    "    in: {" +
                    "      interest: '$$lastLoan.interest'" +
                    "      autoPay: '$$lastLoan.autoPayment'" +
                    "    }" +
                    "  }}," +
                    "  amount: '$currencies.amount'" +
                    "  loanId: { $add: [{ $size: '$loans' }, -1]}" +
                    "}}",
            "{ $match: { 'info.autoPay': true}}",
            "{ $project: {" +
                    "  toPay: {$round: " +
                    "    [{ $add: " +
                    "      [{$sum: " +
                    "        ['$installments.amountLeftToPay']}, '$info.interest']" +
                    "    }, 2]}," +
                    "  amount: 1," +
                    "  autoPay: '$info.autoPay'," +
                    "  'installments._id': 1," +
                    "  'installments.amountLeftToPay': 1," +
                    "   loanId: 1," +
                    "   interest: '$info.interest'" +
                    "}}",
            "{ $project: {" +
                    "  _id: 1," +
                    "  ableToPay: { $gte: ['$amount', '$toPay'] }," +
                    "  installments: 1" +
                    "  interest: 1" +
                    "  loanId: 1" +
                    "  toPay: 1" +
                    "}}",
            "{ $match: {\n" +
                    "  toPay: { $gt: 0}\n" +
                    "}}"
    })
    List<AccountAbleToPay> getIDsOfAccountsWithActiveLoan();
}


