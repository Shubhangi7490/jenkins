import javaposse.jobdsl.dsl.DslFactory

/**
 *  This script contains logic that
 *
 *  - generates views for each deployment pipeline from the REPOS env var
 */


DslFactory dsl = this

// we're parsing the REPOS parameter to retrieve list of repos to build
String repos = binding.variables['REPOS'] ?:
		['https://github.com/marcingrzejszczak/github-analytics',
		 'https://github.com/marcingrzejszczak/github-webhook'].join(',')
List<String> parsedRepos = repos.split(',')
parsedRepos.each {
	String gitRepoName = it.split('/').last() - '.git'
	int customNameIndex = it.indexOf('$')
	int customBranchIndex = it.indexOf('#')
	if (customNameIndex > -1) {
		if (customNameIndex < customBranchIndex) {
			gitRepoName = it.substring(0, customNameIndex)
		}
	}
	String projectName = "${gitRepoName}-pipeline"
	String path = "${projectName}-jobs/${gitRepoName}-pipeline"
	dsl.deliveryPipelineView(path) {
		allowPipelineStart()
		pipelineInstances(5)
		showAggregatedPipeline(true)
		linkToConsoleLog(true)
		columns(1)
		updateInterval(5)
		enableManualTriggers()
		showAvatars()
		showChangeLog()
		pipelines {
			component("Deployment", "${projectName}-build")
		}
		allowRebuild()
		showDescription()
		showPromotions()
		showTotalBuildTime()
		configure {
			(it / 'showTestResults').setValue(true)
			(it / 'pagingEnabled').setValue(true)
		}
	}
}

