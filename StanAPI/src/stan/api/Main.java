package stan.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import stan.Login;
import stan.UserMoreInfo;

public class Main
{
//Поля
    
//Методы
    //конвертация даты
        //полностью
        static public String date_to_string(Date d)
        {
            return "" + y_m_d_toString(d) + "|" + h_m_s_toString(d);
        }
        //год.месяц.число
        static public String y_m_d_toString(Date d)
        {
            return "" + (d.getYear()+1900) + "." + (d.getMonth()+1) + "." + d.getDate();
        }
        //часы:минуты:секунды
        static public String h_m_s_toString(Date d)
        {
            return "" + minutes(d.getHours()+"")+ ":" +minutes(d.getMinutes()+"")+ ":" +minutes(d.getSeconds()+"");
        }
        //добавляем ноль слева, если получается однозначная цыфра
        static public String minutes(String s)
        {
            if (s.length() == 1)
            {
                s = "0" + s;
            }
            return s;
        }
    //работа с List<String>
        //получение изз файла
        static public List<String> Get_String_List(String file) throws IOException, ClassNotFoundException
        {
            List<String> loglist = null;
                try
                {
                    FileInputStream fis = new FileInputStream(file);
                        ObjectInputStream read = new ObjectInputStream(fis);
                                loglist = (List) read.readObject();
                        read.close();
                    fis.close();
                }
                catch (IOException ex)
                {
                }
            return loglist;
        }
        //запись в файл
        static public void Add_String_List(List<String> loglist, String path) throws IOException, FileNotFoundException, ClassNotFoundException
        {
                FileOutputStream fos = new FileOutputStream(path);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(loglist);
                oos.close();
                fos.close();
        }
    //работа с данными пользователей StanleySpace
        //найти Login в List<String>
        static public Login Get_Login(String email, List<String> accounts)
        {
            Login tmp;
            for (String string : accounts)
            {
                tmp = new Login(string);
                if (tmp.GetMail().equalsIgnoreCase(email))    //mail is used
                {
                    return tmp;
                }
            }
            return null;
        }
        //найти UserMoreInfo в List<String>
        static public UserMoreInfo Get_UMI(String email, List<String> accounts)
        {
            UserMoreInfo tmp;
            for (String string : accounts)
            {
                String[] str_tmp = string.split("\n");
                tmp = new UserMoreInfo(str_tmp[0],str_tmp[1]);
                if (tmp.GetMail().equalsIgnoreCase(email))    //mail is used
                {
                    return tmp;
                }
            }
            return null;
        }
    
//Конструкторы
    
}