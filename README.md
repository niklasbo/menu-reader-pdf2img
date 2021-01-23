# Menu Reader: PDF to Image Service

Handles website crawling and converting pdf to image 

## API
| Path          | Header       | Function                                                |
|---------------|--------------|---------------------------------------------------------|
| /pdf2image    | X-AUTH-TOKEN | crawls the website, finds pdfs and saves it in database |

### Environment Variables 
    
- AUTH_TOKEN, string, secret to secure the routes, example: `mySuperSecretAuthTokenForHttpCalls`

- MENU_BASE_URL, string, base page to crawl, example: `https://www.my-menu-provider.de/`

- MENU_OVERVIEW_URL_ENDING, string, specific page which contains links to the pdfs, example: `speiseplan.html`

- MENU_OVERVIEW_HREF_IDENTIFIER, string, the part which identify a link to a pdf `<a href=Identifier>`, example: `assets/context/my-menu-provider/Speiseplan/`

- MONGODB_CONNECTION_STRING, string, connection string, example: `mongodb+srv://user:password@cluster/dbname?retryWrites=true&w=majority`

- MONGODB_DATABASE, string, database name, example: `menudb`

- MONGODB_COLLECTION, string, collection to write the base64 encoded images to, example: `weeknum-and-image`

- IMAGE_DPI, int, image quality in dpi, example: `160`

- PDF_PAGE_NUMBER, int, page number of menu in pdfs, example: `1`
