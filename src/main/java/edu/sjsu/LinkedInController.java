package edu.sjsu;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
/**
 * Created by vipul on 4/17/2015.
 */
@Controller
@RequestMapping("/")
public class LinkedInController {

    private LinkedIn linkedIn;
    ApplicationContext context= new AnnotationConfigApplicationContext(SpringMongoConfig.class);
    MongoOperations mongoOperations= (MongoOperations)context.getBean("mongoTemplate");

    private ConnectionRepository connectionRepository;

    @Inject
    public LinkedInController(LinkedIn linkedIn, ConnectionRepository connectionRepository) {
        this.linkedIn = linkedIn;
        this.connectionRepository = connectionRepository;
    }

    @RequestMapping(method=RequestMethod.GET)
    public String helloLinkedIn(Model model) {
        if (connectionRepository.findPrimaryConnection(LinkedIn.class) == null) {
            return "redirect:/connect/linkedin";
        }

        model.addAttribute(linkedIn.profileOperations().getUserProfile());

        List<LinkedInProfile> connections = linkedIn.connectionOperations().getConnections();
        ArrayList<String> skills=(ArrayList<String>) linkedIn.profileOperations().getUserProfileFull().getSkills();

        Skills skillpersist= new Skills();
        skillpersist.setSkill(skills);
        skillpersist.setUserId(linkedIn.profileOperations().getUserProfileFull().getFirstName() + " " + linkedIn.profileOperations().getUserProfileFull().getLastName()+" "+linkedIn.profileOperations().getProfileId());
        mongoOperations.save(skillpersist);

        model.addAttribute("connections", connections);
        model.addAttribute("skills",skills);
        return "helloskills";
    }

    @RequestMapping(value = "/connections",method=RequestMethod.GET)
    public String onluConnections(Model model) {
        if (connectionRepository.findPrimaryConnection(LinkedIn.class) == null) {
            return "redirect:/connect/linkedin";
        }

        model.addAttribute(linkedIn.profileOperations().getUserProfile());

        List<LinkedInProfile> connections = linkedIn.connectionOperations().getConnections();

        model.addAttribute("connections", connections);
        return "connections";
    }

}
