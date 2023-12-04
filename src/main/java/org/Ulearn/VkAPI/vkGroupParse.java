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
import org.Ulearn.Database.DatabaseConnection;
import org.json.JSONArray;

public class vkGroupParse {
    public List<String> parseBdate() throws IOException, URISyntaxException {
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

        requestUrl = String.format("https://api.vk.com/method/users.get?user_ids=%s&fields=bdate&access_token=%s&v=5.131", userIdsString, Token);
        url = new URI(requestUrl).toURL();
        scanner = new Scanner((InputStream) url.getContent());
        result = "";
        while (scanner.hasNext()) {
            result += scanner.nextLine();
        }

        JSONArray users = new JSONObject(result).getJSONArray("response");
        List<String> usersWithBdate = new ArrayList<>();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            if (user.has("bdate")) {
                String fullNameAndBdate = user.getString("last_name") + " " + user.getString("first_name") + ", " + user.getString("bdate");
                usersWithBdate.add(fullNameAndBdate);
            }
        }
        return usersWithBdate;
    }
}