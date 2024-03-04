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

    public Long createUser(User user) {
        Long id = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    id = resultSet.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public User findUserById(Long userId){
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
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public User findUserByName(String userName) {
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
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public List<User> findAllUser() {
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
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return userList;
    }

    public User updateUser(User user) {
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
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    public void deleteUser(Long userId) {
        try {
            connection = customDataSource.getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public User mapResultsetToUser(ResultSet rs) {
        try {
            return new User(
                    rs.getLong("id"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getInt("age")
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public static void main(String[] args) {
//       User user = new User(3L, "Davron", "Abdusalomov", 20);
//        SimpleJDBCRepository simpleJDBCRepository = new SimpleJDBCRepository();
//        System.out.println(simpleJDBCRepository.createUser(user));
//        System.out.println(simpleJDBCRepository.findUserById(1L));
//        System.out.println(simpleJDBCRepository.findAllUser());
//        System.out.println(simpleJDBCRepository.updateUser(user));
//
//    }

}
