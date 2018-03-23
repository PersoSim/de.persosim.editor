# Releasetests de.persosim.editor.rcp

This document describes validation tests that shall be performed on the final product artifacts immediately before publishing. These tests focus on overall product quality and completeness (e.g. inclusion/integration of required features). For a complete test coverage please also refer to tests defined in the according bundles.

1. [ ] __Run PersoSim Editor__  
Run PersoSim Editor and check the following
	- [ ] GUI started
	- [ ] A default personalization is loaded
	- [ ] About dialog reflects correct information (e.g. version number)
	- [ ] File opening and saving works as expected (verify changes in *.perso file)
	- [ ] Switching personalizations using the "Profiles" menu works and does not carry over changes made in the editor
		- [ ] Warning if unsaved changes are present
	- [ ] Editing contents
		- [ ] Data group content changes are reflected in the data group overview display
		- [ ] PIN object is modifiable
	- [ ] Resigning a personalization
	- [ ] Adding and removing datagroups
	- [ ] Closing the editor
		- [ ] Warning if unsaved changes are present

1. [ ] __Test platform independence__
The basic functionality must be tested for all platforms (e.g. starting and saving a changed personalization file)
	- [ ] Linux
	- [ ] Windows
		- [ ] x86
		- [ ] x64
	- [ ] MacOS X

<p style="page-break-after: always"/>

