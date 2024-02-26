import CodeBlock from '@theme/CodeBlock';
import Heading from "@theme/Heading";

export default function RateLimitHowToUse({method}) {

  const rateLimitUrl = 'https://github.com/giovannymassuia/minimalist-java/blob/main/modules/http-api/src/main/java/io/giovannymassuia/minimalist/java/lib/ratelimiter/RateLimitFactory.java#L34-L40'

  return (
      <>
        <p>When creating your API, you can use the <a href="#factory">RateLimitFactory</a> to create
          a leaking bucket rate limit.</p>

        <CodeBlock language='java' showLineNumbers>
          {`Api.create(8080)
          // highlight-start
      .rateLimit(RateLimitFactory.${method})
          // highlight-end
      .addRoute(Route.builder("/"),
      .path(RouteMethod.GET, "/", controller::handler))
      .start();
          `}
        </CodeBlock>

        <Heading as='h3' id="factory">Factory</Heading>

        <p>
          The <a href={rateLimitUrl} target="_blank">RateLimitFactory</a> provides
          a <code>default</code> and a <code>custom</code> method for this rate limiter.
        </p>

        <ul>
          <li>
            For more details on the check the source code of the&nbsp;
            <a href={rateLimitUrl} target="_blank">RateLimitFactory</a>
          </li>
        </ul>
      </>
  );
}
