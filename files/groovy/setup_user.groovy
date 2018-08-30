import groovy.json.JsonSlurper
import org.sonatype.nexus.security.user.UserManager
import org.sonatype.nexus.security.user.UserNotFoundException
import org.sonatype.nexus.security.user.UserStatus

parsed_args = new JsonSlurper().parseText(args)

try {
    // update an existing user - use default user manager to prevent trying to write to LDAP usermanager.
    user = security.securitySystem.getUserManager(UserManager.DEFAULT_SOURCE).getUser(parsed_args.username)
    user.setFirstName(parsed_args.first_name)
    user.setLastName(parsed_args.last_name)
    user.setEmailAddress(parsed_args.email)
    if (parsed_args.status) {
        user.setStatus(UserStatus.valueOf(parsed_args.status))
    }
    security.securitySystem.updateUser(user)
    security.setUserRoles(parsed_args.username, parsed_args.roles)
    security.securitySystem.changePassword(parsed_args.username, parsed_args.password)
} catch(UserNotFoundException ignored) {
    // create the new user
    security.addUser(parsed_args.username, parsed_args.first_name, parsed_args.last_name, parsed_args.email, true, parsed_args.password, parsed_args.roles)
}
