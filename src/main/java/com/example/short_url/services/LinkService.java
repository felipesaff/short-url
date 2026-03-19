package com.example.short_url.services;

import com.example.short_url.domain.link.Link;
import com.example.short_url.domain.user.User;
import com.example.short_url.repositories.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final Base62Service base62Service;

    public Link createLink(User user, String url) {
        try {
            Link link = new Link();
            link.setUser(user);
            link.setUrl(url);
            return linkRepository.save(link);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param shortCode The short code of the link
     * Increments the click count for the link and returns the original URL
     * @return String
     */
    public String getOriginalUrlToRedirect(String shortCode) {
        Long linkId = base62Service.decode(shortCode);
        return linkRepository.findById(linkId)
                .map(link -> {
                    incrementClicks(linkId);
                    return link.getUrl();
                })
                .orElseThrow(() -> new RuntimeException("Link not found"));
    }

    public void incrementClicks(Long linkId) {
        linkRepository.findById(linkId).ifPresent(link -> {
            link.setClicks(link.getClicks() + 1);
            linkRepository.save(link);
        });
    }
}
