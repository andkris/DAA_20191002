import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Config {

	private static String dateFromStr = "2017.03.01";
	private static String dateToStr = "2017.03.31";

	private static Date dateFrom;
	
	private static Date dateTo;


    public static String getDateFromStr() {
        return dateFromStr;
    }

    public static String getDateToStr() {
        return dateToStr;
    }
        
    public static void prepareData() throws ParseException{
		DateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
        dateFrom = formatter.parse(Config.dateFromStr);
        dateTo = formatter.parse(Config.dateToStr);
	}
	
	public static Date getDateFrom() {
		return dateFrom;
	}

	public static Date getDateTo() {
		return dateTo;
	}
	
}