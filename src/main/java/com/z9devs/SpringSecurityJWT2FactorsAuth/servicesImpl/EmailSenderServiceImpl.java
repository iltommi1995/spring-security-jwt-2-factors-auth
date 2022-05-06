package com.z9devs.SpringSecurityJWT2FactorsAuth.servicesImpl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.z9devs.SpringSecurityJWT2FactorsAuth.services.EmailSenderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {
	// Classe di spring email usata per inviare mail,
	// bisogna ricordarsi di configurare nel file di properties
	// le credenziali del servizio di mail utilizzato
	@Autowired
	private JavaMailSender mailSender;
	
	@Override
	@Async
	public void send(String to, String email) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			helper.setText(email, true);
			helper.setTo(to);
			helper.setSubject("Confirm your email");
			helper.setFrom("provaspringsec@z9devs.com");
			mailSender.send(mimeMessage);
		} catch (MessagingException e) { 
			log.error("failed to send email", e);
			throw new IllegalStateException("Failed to send email");
		}
	}
}
