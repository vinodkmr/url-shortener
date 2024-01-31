package dev.vinodkmr.controller;

import dev.vinodkmr.service.BotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;
@Slf4j
@Controller
@RequiredArgsConstructor
public class ShortenerController {

    private final BotService botService;
    @GetMapping("/{shortUrl}")
    public RedirectView redirectToLongUrl(@PathVariable("shortUrl") String shortUrl){
        log.info("Short Url Path Param:{}",shortUrl);
        String longUrl = botService.getLongUrlByShortUrl(shortUrl);
        return new RedirectView(longUrl);
    }
}
