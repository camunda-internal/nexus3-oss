import groovy.json.JsonSlurper

parsed_args = new JsonSlurper().parseText(args)

if (repository.getRepositoryManager().exists(parsed_args.name)) {
  repository.getRepositoryManager().delete(parsed_args.name)
}

