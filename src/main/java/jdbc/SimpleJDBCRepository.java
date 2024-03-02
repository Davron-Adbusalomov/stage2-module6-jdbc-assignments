package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {
    CustomConnector customConnector = getCustomConnector();

    CustomDataSource customDataSource = CustomDataSource.getInstance();

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) values(?,?,?)";
    private static final String updateUserSQL = "UPDATE myusers set firstname = ?, lastname=?, age=? where id = ?";
    private static final String deleteUser = "DELETE FROM myusers where id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers where id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers where firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) throws SQLException {
        try {
        connection = customDataSource.getConnection();
        ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setInt(3, user.getAge());
        ps.executeUpdate();

        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
            return null;
    }

    public User findUserById(Long userId) throws SQLException {
        try {
            connection = customDataSource.getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()){
               return mapResultsetToUser(resultSet);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.close();
        }
        return null;
    }

    public User findUserByName(String userName) throws SQLException {
        try {
            connection = customDataSource.getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1,userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return mapResultsetToUser(rs);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
        connection.close();
    }
        return null;
    }

    public List<User> findAllUser() throws SQLException {
        List<User> userList = new ArrayList<>();
        try {
            connection = customDataSource.getConnection();
            ps = connection.prepareStatement(findAllUserSQL);

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                userList.add(mapResultsetToUser(resultSet));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.close();
        }
        return userList;
    }

    public User updateUser(User user) throws SQLException {
        try {
            connection = customDataSource.getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                return findUserById(user.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        return null;
    }
    public void deleteUser(Long userId) throws SQLException {
        try {
            connection = customDataSource.getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            connection.close();
        }
    }

    public User mapResultsetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getInt("age")
        );
    }
}
