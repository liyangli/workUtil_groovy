import java.text.ParseException
import java.text.SimpleDateFormat

/**
 *
 * User: liyangli
 * Date: 2016/7/11
 * Time: 16:40
 */

String dateString = "2012-12-06 12:32:23";

try
{
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = sdf.parse(dateString);
    print(date.format("yyyy-MM-dd  HH:mm:ss"))
}
catch (ParseException e)
{
    System.out.println(e.getMessage());
}