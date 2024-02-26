import CodeBlock from '@theme/CodeBlock';

import current_version from "@site/current_version.json";

export default function PomDependency({artifactId}) {
  return (
      <CodeBlock language='xml' title='pom.xml'>
        {`<dependency>
  <groupId>com.github.giovannymassuia</groupId>
  <artifactId>${artifactId}</artifactId>
  <version>${current_version.version}</version>
</dependency>
`}
      </CodeBlock>
  );
}
