import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';
import currentVersion from './current_version.json';

const config: Config = {
  title: 'minimalist-java',
  tagline: 'A minimalist Java framework',
  favicon: 'img/favicon.ico',

  plugins: [require.resolve("@cmfcmf/docusaurus-search-local")],

  // Set the production url of your site here
  url: 'https://docs.minimalist-java.giovannymassuia.io',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'giovannymassuia', // Usually your GitHub org/user name.
  projectName: 'minimalist-java', // Usually your repo name.

  onBrokenLinks: 'warn',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          sidebarPath: './sidebars.ts',

          showLastUpdateAuthor: true,
          showLastUpdateTime: true,

          includeCurrentVersion: true,
          lastVersion: 'current',
          versions: {
            current: {
              label: currentVersion.version,
              badge: true
            }
          }

          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          // editUrl:
          //     'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        blog: {
          showReadingTime: true,
          readingTime: ({content, defaultReadingTime}) =>
              defaultReadingTime({content, options: {wordsPerMinute: 100}}),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          // editUrl:
          //     'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    // Replace with your project's social card
    image: 'img/minimalist-java-social-card.png',
    colorMode: {
      defaultMode: 'dark'
    },
    navbar: {
      title: 'minimalist-java',
      logo: {
        alt: 'minimalist-java logo',
        src: 'img/minimalist-java-no-bg.png',
      },
      items: [
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'left',
          label: 'Docs',
        },
        {to: '/blog', label: 'Blog', position: 'left'},
        {
          type: 'docsVersionDropdown',
          position: 'right',
          dropdownItemsAfter: [
            {type: 'html', value: '<hr class="dropdown-separator">'},
            {to: '/versions', label: 'All versions'}
          ],
          dropdownActiveClassDisabled: true,
        },
        {
          href: 'https://github.com/giovannymassuia/minimalist-java',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'Docs',
              to: '/docs/intro',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Github Issues',
              href: 'https://github.com/giovannymassuia/minimalist-java/issues',
            },
            // {
            //   label: 'Discord',
            //   href: 'https://discordapp.com/invite/docusaurus',
            // },
            // {
            //   label: 'Twitter',
            //   href: 'https://twitter.com/docusaurus',
            // },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'Blog',
              to: '/blog',
            },
            {
              label: 'GitHub',
              href: 'https://github.com/giovannymassuia/minimalist-java',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} minimalist-java, Inc. Built with Docusaurus.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
      additionalLanguages: ["java", "scala"],
    },
  } satisfies Preset.ThemeConfig,
};

export default config;
