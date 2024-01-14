# Dependencies

_This page describes planned features, and has no implementation yet_

fiz build will support dependencies defined in a `dependencies.smp` file.
Dependencies will be fetched from git repositories, and can be specified by tag, hash, or branch.
fiz build is designed to build from source without a package host.

Example of a dependency:

```
dependencies: [
	{
		url: "https://github.com/cliserkad/fiz";
		tag: "v0.0.0";
		// hash: "a1b2c3d";
		// branch: "main";
	},
];
```
