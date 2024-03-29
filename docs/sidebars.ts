import type {SidebarsConfig} from '@docusaurus/plugin-content-docs';

/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */
const sidebars: SidebarsConfig = {
  // By default, Docusaurus generates a sidebar from the docs folder structure
  // tutorialSidebar: [{type: 'autogenerated', dirName: '.'}],

  // But you can create a sidebar manually
  tutorialSidebar: [
    'intro',
    {
      type: 'category',
      label: 'Installation',
      link: {
        type: 'generated-index',
        title: 'Installation',
        description: 'Installation documentation',
        keywords: ['installation', 'documentation'],
      },
      items: [{type: 'autogenerated', dirName: 'installation'}],
    },
    {
      type: 'category',
      label: 'Modules',
      collapsible: false,
      link: {
        type: 'generated-index',
        title: 'Modules',
        slug: 'modules',
        description: 'Modules documentation',
        keywords: ['modules', 'documentation'],
      },
      items: [{type: 'autogenerated', dirName: 'modules'}],
    },
    {
      type: 'html',
      value: '<hr/>'
    },
    {
      type: 'category',
      label: 'Docusaurus Reference',
      items: [{type: 'autogenerated', dirName: 'docusaurus-tutorial'}],
    }

    // 'intro',
    // 'hello',
    //   {
    //     type: 'category',
    //     label: 'Tutorial',
    //     items: ['tutorial-basics/create-a-document'],
    //   },
  ],
};

export default sidebars;
