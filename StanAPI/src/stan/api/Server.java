package stan.api;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import stan.Login;
import stan.Message;
import stan.Registration;
import stan.StanError;
import stan.UserMoreInfo;

public class Server
{
//Поля
    static public String accpath;
    static public String logins;
    
//Методы
    static public boolean Answer(String from, ObjectOutputStream os, Object answer, String submessage)
    {
        try
        {
            os.writeObject(answer);//и ответить соответственно клиенту
            return false;
        }
        catch (IOException ex)
        {
            StanLog.add_log(1,from,"(Answer) проблема с записью объекта" +"\n" +
                    submessage+"\n" +ex.toString());
        }
        return true;//не позволяем программе дальше обрабатывать информацию
    }
    static public List<String> G_S_L(String from,String path, ObjectOutputStream os)
    {
        List<String> bmlist;
        try
        {
            bmlist = Main.Get_String_List(path); //список с данными пользователей
            if(bmlist == null)//если списка не существует
            {
                bmlist = new ArrayList();//его нужно создать
                File f = new File(path);
                if(!f.exists())
                {
                    f.createNewFile();
                }
                Main.Add_String_List(bmlist, path);//и записать в файл
            }
            return bmlist;//всё круто
        }
        catch (IOException ex)
        {
            StanLog.add_log(1,from,"(G_S_L) проблема с чтением из файла" +"\n" +
                    " неудачная попытка получить список пользователей"+"\n" +ex.toString());
            Answer(from, os, (Object) new StanError("ReadAllObjectsError"),"неудачная попытка ответить клиенту что чтение из файла не удалось");//оповещаем клиента о том, что неудачная попытка получить список пользователей
        }
        catch (ClassNotFoundException ex)
        {
            StanLog.add_log(1,from,"(G_S_L) проблема с классами" +"\n" +
                    " класс который достаём не тот BaseMessage"+"\n" +ex.toString());
            Answer(from, os, (Object) new StanError("ClassNotFoundError"),"неудачная попытка ответить клиенту что класс который получили не тот BaseMessage");//оповещаем клиента о том, что класс который получили не тот BaseMessage
        }
        return null;//не позволяем программе дальше обрабатывать информацию
    }
    static public boolean A_S_L(String from,List<String> bmlist, String path, ObjectOutputStream os)
    {
        try
        {
            Main.Add_String_List(bmlist, path);//и записать в файл
            return false;
        }
        catch (IOException ex)
        {
            StanLog.add_log(1,from,"(Add_String_List) проблема с записью в файл для регистрации" +"\n" +
                    "неудачная попытка записать список регистрирующихся"+"\n" +ex.toString());
            Answer(from, os, (Object) new StanError("WriteAllObjectsError"),"попытка ответить клиенту что запись в файл не удалось");//оповещаем клиента о том, что неудачная попытка записать список объектов лога
        }
        catch (ClassNotFoundException ex)
        {
            StanLog.add_log(1,from,"(Add_String_List) проблема с классами" +"\n" +
                    "класс который получили не тот String"+"\n" +ex.toString());
            Answer(from, os, (Object) new StanError("ClassNotFoundError"),"неудачная попытка ответить клиенту что класс который получили не тот String");//оповещаем клиента о том, что класс который получили не тот BaseMessage
        }
        return true;//не позволяем программе дальше обрабатывать информацию
    }
    //
    static public boolean New_User(Registration reg, List<String> userlist, List<String> loginslist, ObjectOutputStream outputStream)
    {
        Login lgn = Main.Get_Login(reg.GetNewLogin().GetMail(), loginslist);//попытка добыть объект данных пользователя по заданному логину
        if(lgn == null)//если не добыли (это хорошо, потому что мыло не занято)
        {
            lgn = reg.GetNewLogin();
            UserMoreInfo umi = reg.GetNewUMI();
            userlist.add(umi.toString());//добавляем в список новобранца
            loginslist.add(lgn.toString());//добавляем в список новобранца
            //и записываем в файл
            if(Server.A_S_L("Messaging",userlist, accpath, outputStream))//и если запись прошла успешно то продолжаем
            {
                StanLog.add_log(1,"Messaging","Add userlist failed");
                Server.Answer("Messaging",outputStream, (Object) new StanError("WriteListErrorAcc"),"неудачная попытка ответить клиенту, что проблема с записью списка аккаунтов");//оповещаем клиента о том, что проблема с записью списка аккаунтов
                return false;//а если не успешно, то не позволяем программе дальше обрабатывать информацию
            }
            if(Server.A_S_L("Messaging",loginslist, logins, outputStream))//и если запись прошла успешно то продолжаем
            {
                StanLog.add_log(1,"Messaging","Add loginslist failed");
                Server.Answer("Messaging",outputStream, (Object) new StanError("WriteListErrorLogins"),"неудачная попытка ответить клиенту, что проблема с записью списка логинов");//оповещаем клиента о том, что проблема с записью списка логинов
                return false;//а если не успешно, то не позволяем программе дальше обрабатывать информацию
            }
            StanLog.add_log(0,"Messaging","Registration successful "+lgn.GetMail());
            if(Server.Answer("Messaging",outputStream, (Object) new Message("RegistrationSuccessful"),"неудачная попытка ответить клиенту что всё прошло успешно"))//оповещаем клиента о том, что всё прошло успешно
            {
                return false;//не позволяем программе дальше обрабатывать информацию
            }
            StanLog.add_log(0,"Messaging","Registration request send");
        }
        else//а если достали
        {
            StanLog.add_log(2,"Messaging","Mail is used "+lgn.GetMail());//такой электронный адресс уже используется
            if(Server.Answer("Messaging",outputStream, (Object) new StanError("MailIsUsed"),"неудачная попытка ответить клиенту что такой электронный адресс уже используется"))//оповещаем клиента о том, что такой электронный адресс уже используется
            {
                return false;//не позволяем программе дальше обрабатывать информацию
            }
            StanLog.add_log(0,"Messaging","Mail is used send");
        }
        return true;
    }
    //
    static public void SetProp(String a,String lo)
    {
        accpath=a;
        logins = lo;
    }
    
}