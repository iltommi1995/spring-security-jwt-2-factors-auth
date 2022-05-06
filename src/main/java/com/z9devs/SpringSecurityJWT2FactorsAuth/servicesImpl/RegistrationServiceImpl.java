package com.z9devs.SpringSecurityJWT2FactorsAuth.servicesImpl;

import java.time.LocalDateTime;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.ConfirmationToken;
import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.RegistrationRequest;
import com.z9devs.SpringSecurityJWT2FactorsAuth.entities.User;
import com.z9devs.SpringSecurityJWT2FactorsAuth.services.ConfirmationTokenService;
import com.z9devs.SpringSecurityJWT2FactorsAuth.services.EmailSenderService;
import com.z9devs.SpringSecurityJWT2FactorsAuth.services.RegistrationService;
import com.z9devs.SpringSecurityJWT2FactorsAuth.services.UserService;
import com.z9devs.SpringSecurityJWT2FactorsAuth.utils.EmailValidator;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private UserService userService;
	private EmailValidator emailValidator;
	private ConfirmationTokenService confirmationTokenService;
	private final EmailSenderService emailSenderService;
	
	// Qui viene presa la richiesta di registrazione e deve esserci la 
	// logica per capire se i dati vanno bene
	@Override
	public String register(RegistrationRequest request) {
		// TODO -> capire se la mail e user sono validi:
		//		 	- Vedere che non ci sia già nel db
		//			- Vedere se mail esiste
		boolean isValidEmail = emailValidator.test(request.getEmail());
		if(!isValidEmail) {
			throw new IllegalStateException("Email not valid");
		}
		
		// Salvo lo user e creo il token da inviare via mail
		String token = userService.signUpUser(
			new User(
				request.getFirstName(),
				request.getLastName(),
				request.getUsername(),
				request.getEmail(),
				request.getPassword()
			)
		);
		
		// Invio la mail con il link contenente il token di verifica
		String link = "http://localhost:8080/api/registration/confirm?token=" + token;
		emailSenderService.send(request.getEmail(), buildEmail(request.getFirstName(), link));
		return String.format("Verification mail sent to %s", request.getEmail());
	}
	
	@Override
	public String confirmToken(String token) {
		// Recupero il token dal db (se esiste)
		ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
				.orElseThrow(() -> new IllegalStateException("Token not found"));
		
		// Se risulta già confermato dico che la mail è già stata confermata
		if(confirmationToken.getConfirmedAt() != null) {
			throw new IllegalStateException("Email already confirmed");
		}
		
		// Se il token è scaduto invio errore
		LocalDateTime expiredAt = confirmationToken.getExpiresAt();
		
		if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired");
        }
		
		// Setto il token come confermato
		confirmationTokenService.setConfirmedAt(token);
		// Abilito l'user
        userService.enableUser(
                confirmationToken.getUser().getEmail());
		
		return "Account confirmed, now you can login";
	
	}
	
	private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}
