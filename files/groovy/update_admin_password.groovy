import groovy.json.JsonSlurper

parsed_args = new JsonSlurper().parseText(args)

security.securitySystem.changePassword(parsed_args.user, parsed_args.new_password)
