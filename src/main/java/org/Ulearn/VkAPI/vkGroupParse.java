package org.Ulearn.VkAPI;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.json.JSONArray;

public class vkGroupParse {
    public class StudentInfoVkApi {
        String fullName;
        String bdate;
        String sex;
        String country;
        List<String> schools;
    
        public StudentInfoVkApi(String fullName, String bdate, String sex, String country, List<String> schools) {
            this.fullName = fullName;
            this.bdate = bdate;
            this.sex = sex;
            this.country = country;
            this.schools = schools;
        }

        public String getFullName() {
            return fullName;
        }

        public String getBdate() {
            return bdate;
        }

        public String getSex() {
            return sex;
        }

        public String getCountry() {
            return country;
        }

        public List<String> getSchools() {
            return schools;
        }
    }

    public List<StudentInfoVkApi> parseUserData() throws IOException, URISyntaxException {
        Dotenv dotenv = Dotenv.load();
        String Token = dotenv.get("VK_TOKEN");
        String groupId = "basicprogrammingrtf2023";
        String requestUrl = String.format("https://api.vk.com/method/groups.getMembers?group_id=%s&access_token=%s&v=5.131", groupId, Token);
        URL url = new URI(requestUrl).toURL();
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()) {
            result += scanner.nextLine();
        }
        JSONObject responseJson = new JSONObject(result);
        JSONArray userIds = responseJson.getJSONObject("response").getJSONArray("items");
    
        String userIdsString = Arrays.stream(userIds.toList().toArray(new Integer[0]))
            .map(String::valueOf)
            .collect(Collectors.joining(","));
    
        requestUrl = String.format("https://api.vk.com/method/users.get?user_ids=%s&fields=bdate,sex,country,schools&access_token=%s&v=5.131", userIdsString, Token);
        url = new URI(requestUrl).toURL();
        scanner = new Scanner((InputStream) url.getContent());
        result = "";
        while (scanner.hasNext()) {
            result += scanner.nextLine();
        }
        JSONArray users = new JSONObject(result).getJSONArray("response");
        List<StudentInfoVkApi> students = new ArrayList<>();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String fullName = user.getString("last_name") + " " + user.getString("first_name");
            String bdate = user.has("bdate") ? user.getString("bdate") : null;
            String sex = user.has("sex") ? (user.getInt("sex") == 1 ? "Female" : "Male") : null;
            String country = user.has("country") ? user.getJSONObject("country").getString("title") : null;
            List<String> schools = new ArrayList<>();
            if (user.has("schools")) {
                JSONArray schoolsJson = user.getJSONArray("schools");
                for (int j = 0; j < schoolsJson.length(); j++) {
                    schools.add(schoolsJson.getJSONObject(j).getString("name"));
                }
            }
            students.add(new StudentInfoVkApi(fullName, bdate, sex, country, schools));
        }
        System.out.println(students.size());
        return students;
    }
}