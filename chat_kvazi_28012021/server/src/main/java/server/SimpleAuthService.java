package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService {

    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;


    private class UserData {
        String login;
        String password;
        String nickname;

        public UserData(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }

    private List<UserData> users;

    public SimpleAuthService() throws SQLException {
        users = new ArrayList<>();
        users.add(new UserData("qwe", "qwe", "qwe"));
        users.add(new UserData("asd", "asd", "asd"));
        users.add(new UserData("zxc", "zxc", "zxc"));

        connectToBD();

        while (resultSet.next()) {
            users.add(new UserData(resultSet.getString("login"), resultSet.getString("password"), resultSet.getString("nickname")));

        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (UserData user : users) {
            if (user.login.equals(login) && user.password.equals(password)) {
                return user.nickname;
            }
        }

        return null;
    }
    public void connectToBD() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:CHAT.kvazi");
        statement = connection.createStatement();
        resultSet = statement.executeQuery("SELECT * FROM user");
    }

    @Override
    public boolean registration(String login, String password, String nickname) throws SQLException {
        for (UserData user : users) {
            if (user.login.equals(login) || user.nickname.equals(nickname)) {
                return false;
            }
        }
        users.add(new UserData(login, password, nickname));
        statement.execute("INSERT INTO 'user' ('login', 'password', 'nickname') VALUES ('LOGIN' , 'PASSWORD', 'NICKNAME')".replace("LOGIN", login).replace("PASSWORD", password).replace("NICKNAME", nickname));
        return true;
    }

    public boolean changeNickname(String login, String password, String newNickname) throws SQLException {

        for (UserData user: users) {
            if (user.login.equals(login) && user.password.equals(password)){
                if (user.nickname == newNickname){
                    System.out.println("такой ник уже существует");
                    return false;
                }else{
                    user.nickname = newNickname;
                    statement.execute(String.format("UPDATE users SET nickname='%s' WHERE login = '%s' AND password = '%s'", newNickname, login, password));
                    return true;
                }

            }
        }
        return true;
    }
}
