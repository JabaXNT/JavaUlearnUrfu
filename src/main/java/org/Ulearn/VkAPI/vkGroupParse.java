package org.Ulearn.VkAPI;

import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class vkGroupParse {
    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String Token = dotenv.get("VK_TOKEN");
        String groupId = "basicprogrammingrtf2023";
        String requestUrl = String.format("https://api.vk.com/method/groups.getMembers?group_id=%s&access_token=%s&v=5.131", groupId, Token);
        URL url = new URL(requestUrl);
        Scanner scanner = new Scanner((InputStream) url.getContent());
        String result = "";
        while (scanner.hasNext()) {
            result += scanner.nextLine();
        }
        System.out.println(result);
    }
}