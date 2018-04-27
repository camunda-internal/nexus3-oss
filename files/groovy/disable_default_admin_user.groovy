import org.sonatype.nexus.security.user.UserNotFoundException
import org.sonatype.nexus.security.user.UserStatus

try {
  def user = security.securitySystem.getUser('admin')
  user.setStatus(UserStatus.disabled)
  security.securitySystem.updateUser(user)
} catch (UserNotFoundException e) {
  // do nothing
}
