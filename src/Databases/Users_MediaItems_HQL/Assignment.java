package Databases.Users_MediaItems_HQL;

import test.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created By: Assaf, On 06/12/2020
 * Description:
 */
public class Assignment {

    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    // QUERIES ==============================================
    private static String IS_USERNAME_EXIST = "from Users u where u.username = :name";
    private static String TOP_N_ITEMS = "from Mediaitems mi order by mi.mid desc";
    private static String VALIDATE_USER = "from Users u where u.username = :username and u.password = :password";
    private static String VALIDATE_ADMIN = "from AdministratorsEntity a where a.username = :username and a.password = :password";
    private static String GET_HISTORY = "from HistoryEntity h where h.userid = :uid order by h.viewtime asc";
    private static String GET_USERS = "from Users";
    // ======================================================

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static boolean isExistUsername (String username)
    {
        try(Session session = getSession())
        {
            Query query = session.createQuery(IS_USERNAME_EXIST);

            query.setParameter("name",username);

            return query.list().size() > 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public static String insertUser(String username, String password, String first_name, String last_name, String day_of_birth, String month_of_birth, String year_of_birth)
    {
        if(isExistUsername(username)) return null;

        try(Session session = getSession())
        {
            int day = Integer.parseInt(day_of_birth);
            int month = Integer.parseInt(month_of_birth);
            int year = Integer.parseInt(year_of_birth);

            Transaction tx = session.beginTransaction();

            Users user = new Users();
            user.setUsername(username);
            user.setPassword(password);
            user.setFirstName(first_name);
            user.setLastName(last_name);

            Calendar calendar = Calendar.getInstance();
            // current time stamp
            user.setRegistrationDate(Timestamp.from(calendar.toInstant()));
            // date of birth
            calendar.set(year, month - 1, day, 0, 0,0);
            user.setDateOfBirth(Timestamp.from(calendar.toInstant()));

            long user_id = (long)session.save(user);
            tx.commit();

            return "" + user_id;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Mediaitems> getTopNItems(int top_n)
    {
        try(Session session = getSession())
        {
            return session.createQuery(TOP_N_ITEMS).setMaxResults(top_n).list();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String validateUser(String username, String password)
    {
        try(Session session = getSession())
        {
            Query query = session.createQuery(VALIDATE_USER);

            query.setParameter("username",username);
            query.setParameter("password",password);

            List<Users> validated_user = query.list();
            if(validated_user.size() > 0)
            {
                return "" + validated_user.get(0).getUserid();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "Not Found";
    }

    public static String validateAdministrator (String username, String password)
    {
        try(Session session = getSession())
        {
            Query query = session.createQuery(VALIDATE_ADMIN);

            query.setParameter("username",username);
            query.setParameter("password",password);

            List<AdministratorsEntity> validated_admin = query.list();
            if(validated_admin.size() > 0)
            {
                return "" + validated_admin.get(0).getAdminid();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return "Not Found";
    }

    public static void insertToHistory (String userid, String mid)
    {
        try(Session session = getSession())
        {
            int user_id = Integer.parseInt(userid);
            int media_id = Integer.parseInt(mid);

            Transaction tx = session.beginTransaction();

            HistoryEntity historyEntity = new HistoryEntity();
            historyEntity.setUserid(user_id);
            historyEntity.setMid(media_id);

            Timestamp ts = Timestamp.from(Calendar.getInstance().toInstant());
            historyEntity.setViewtime(ts);

            session.save(historyEntity);
            tx.commit();

            System.out.println("The insertion to history table was successful " + ts);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Map<String, Date> getHistory (String userid)
    {
        Map<String, Date> res = new LinkedHashMap<>();

        try(Session session = getSession())
        {
            long uid = Long.parseLong(userid);

            Query query = session.createQuery(GET_HISTORY);

            query.setParameter("uid",uid);

            List<HistoryEntity> history = query.list();
            for(HistoryEntity historyEntity : history)
            {
                Mediaitems media_item = session.get(Mediaitems.class, historyEntity.getMid());
                res.put(media_item.getTitle(),new Date(historyEntity.getViewtime().getTime()));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return res;
    }

    public static void insertToLog (String userid)
    {
        try(Session session = getSession())
        {
            int user_id = Integer.parseInt(userid);

            Transaction tx = session.beginTransaction();

            LoginlogEntity login_entry = new LoginlogEntity();
            login_entry.setUserid(user_id);

            Timestamp ts = Timestamp.from(Calendar.getInstance().toInstant());
            login_entry.setLogintime(ts);

            session.save(login_entry);
            tx.commit();

            System.out.println("The insertion to log table was successful " + ts);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Timestamp subtractDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.HOUR,0);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(calendar.getTime());
        cal.add(Calendar.DATE, -days);

        return Timestamp.from(cal.toInstant());
    }


    public static int getNumberOfRegistredUsers(int n)
    {
        Timestamp date_last_n_days = subtractDays(n);
        int count = 0;

        List<Users> users = getUsers();
        for(Users user : users)
        {
            if(date_last_n_days.before(user.getRegistrationDate())) count++;
        }

        return count;
    }

    public static List<Users> getUsers ()
    {
        try(Session session = getSession())
        {
            return session.createQuery(GET_USERS).list();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static Users getUser (String userid)
    {
        try(Session session = getSession())
        {
            long uid = Long.parseLong(userid);

            return session.get(Users.class,uid);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static void test(boolean con)
    {
        ;
        String res = "test (" + (tid++) + "): ";

        if(con)
        {
            pass_count++;
            res += " Passed.";
        }
        else
        {
            res += " Wrong!.";
        }
        res += " | Total Passed: " + pass_count + " / " + tid;
        System.out.println(res);
    }

    private static int tid = 0;
    private static int pass_count = 0;

    public static void main(String[] args) {
        // media items part test
        test(Assignment.getTopNItems(10).size() == 10); //0
        test(Assignment.getTopNItems(5).size() == 5); //1
        test(Assignment.getTopNItems(0).size() == 0); //2

        // inserts info
//        System.out.println(Assignment.insertUser("amir","abcd","amir","a","1","1","1999")); // uid = 1
//        System.out.println(Assignment.insertUser("assaf","abcd","assaf","b","2","1","1993")); // uid = 2
//        System.out.println(Assignment.insertUser("yoav","abcd","yoav","c","3","2","1990")); // uid = 3
//        System.out.println(Assignment.insertUser("zohar","abcd","zohar","d","4","2","2000")); // uid = 4
//        System.out.println(Assignment.insertUser("tomer","abcd","tomer","e","5","3","1987")); // uid = 5
//        System.out.println(Assignment.insertUser("orit","abcd","orit","f","6","3","1990")); // uid = 6

//        Assignment.insertToHistory("2","11");
//        Assignment.insertToHistory("2","12");
//        Assignment.insertToHistory("2","3");
//        Assignment.insertToHistory("2","4");
//        Assignment.insertToHistory("1","1");
//        Assignment.insertToHistory("4","5");
//        Assignment.insertToHistory("5","5");
//        Assignment.insertToHistory("1","6");

//        Assignment.insertToLog("1");
//        Assignment.insertToLog("2");
//        Assignment.insertToLog("5");
//        Assignment.insertToLog("2");

        // tests
        test(!isExistUsername("")); //3
        test(!isExistUsername("moshe")); //4
        test(isExistUsername("assaf")); //5

        test(validateUser("","").equals("Not Found")); //6
        test(validateUser("assaf","a").equals("Not Found")); //7
        test(validateUser("assaf","abcd").equals("2")); //8

        test(validateAdministrator("","").equals("Not Found")); //9
        test(validateAdministrator("assaf","abcd").equals("Not Found")); //10
        test(validateAdministrator("asa","aaaa").equals("1")); //11

        test(getHistory("0").isEmpty()); //12
        test(getHistory("3").isEmpty()); //13
        test(getHistory("1").size() == 2); //14
        Map res = getHistory("2");
        test(res.size() == 6); //15
        for(Object e : res.entrySet())
        {
            System.out.println(e);
        }

        test(Assignment.getNumberOfRegistredUsers(10) == 6); //16
        test(Assignment.getNumberOfRegistredUsers(0) == 6); //17

        test(Assignment.getUser("11") == null); //18
        test(Assignment.getUser("0") == null); //19
        test(Assignment.getUser("2") != null); //20

        List<Users> users = getUsers();
        test(users.size() == 6); //21
        for(Users u : users)
        {
            System.out.println(u);
        }
    }

}
