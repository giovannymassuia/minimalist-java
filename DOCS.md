## Maven Security

1. **Setup Maven Security**

   Maven provides a master password encryption utility. First, you'll need to encrypt a master
   password:

   ```bash
   mvn --encrypt-master-password <your-master-password>
   ```

   This will give you an encrypted password.

   Create a `settings-security.xml` file in your `~/.m2/` directory (or `%USERPROFILE%\.m2\` on
   Windows) with the following content:

   ```xml
   <settingsSecurity>
     <master>{<your-encrypted-master-password>}</master>
   </settingsSecurity>
   ```

   Replace the value inside the `<master>` tags with your encrypted master password.

2. **Encrypt Your Personal Access Token**

   Next, use the following command to encrypt your GitHub personal access token:

   ```bash
   mvn --encrypt-password <your-personal-access-token>
   ```

   This will give you an encrypted version of your token.

3. **Update `settings.xml`**

   In your `settings.xml`, replace the plain personal access token with the encrypted version:

   ```xml
   <servers>
     <server>
       <id>github</id>
       <username>YOUR_GITHUB_USERNAME</username>
       <password>{<your-encrypted-personal-token>}</password> <!-- Encrypted Token -->
     </server>
   </servers>
   ```

   Replace `{<your-encrypted-personal-token>}` with your encrypted personal access token.

4. **Use Maven As Usual**

   When Maven needs to authenticate, it will use the master password to decrypt your personal access
   token and
   authenticate with GitHub Packages.

This method enhances the security of sensitive information in `settings.xml`. However, remember that
while encryption
makes it harder for someone to get the token, it is not foolproof. The master password and the
encryption mechanism are
known, so if someone gains access to both `settings.xml` and `settings-security.xml`, they can
theoretically decrypt the
password. Still, it's a significant improvement over storing the token in plain text.
