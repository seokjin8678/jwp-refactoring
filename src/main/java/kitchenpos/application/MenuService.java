package kitchenpos.application;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Money;
import kitchenpos.domain.Product;
import kitchenpos.exception.KitchenPosException;
import kitchenpos.repository.MenuGroupRepository;
import kitchenpos.repository.MenuRepository;
import kitchenpos.repository.ProductRepository;
import kitchenpos.ui.dto.MenuCreateRequest;
import kitchenpos.ui.dto.MenuProductCreateRequest;
import kitchenpos.ui.dto.MenuResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;

    public MenuService(
        MenuRepository menuRepository,
        MenuGroupRepository menuGroupRepository,
        ProductRepository productRepository
    ) {
        this.menuRepository = menuRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
    }

    public MenuResponse create(MenuCreateRequest request) {
        Money price = Money.from(request.getPrice());
        List<MenuProductCreateRequest> menuProductCreateRequests = request.getMenuProducts();
        MenuGroup menuGroup = findMenuGroup(request.getMenuGroupId());
        Menu menu = menuRepository.save(new Menu(null, request.getName(), price, menuGroup));
        menu.addMenuProducts(createMenuProducts(menu, menuProductCreateRequests));
        return MenuResponse.from(menu);
    }

    private MenuGroup findMenuGroup(Long menuGroupId) {
        return menuGroupRepository.findById(menuGroupId)
            .orElseThrow(() -> new KitchenPosException("해당 메뉴 그룹이 없습니다. menuGroupId=" + menuGroupId));
    }

    private List<MenuProduct> createMenuProducts(Menu menu, List<MenuProductCreateRequest> requests) {
        List<Product> products = findProducts(requests);
        validateNotExistProducts(products, requests);
        Map<Long, Long> productIdToQuantity = requests.stream()
            .collect(toMap(MenuProductCreateRequest::getProductId, MenuProductCreateRequest::getQuantity));
        return products.stream()
            .map(product -> new MenuProduct(null, productIdToQuantity.get(product.getId()), product, menu))
            .collect(toList());
    }

    private List<Product> findProducts(List<MenuProductCreateRequest> requests) {
        return requests.stream()
            .map(MenuProductCreateRequest::getProductId)
            .collect(collectingAndThen(toList(), productRepository::findAllByIdIn));
    }

    private void validateNotExistProducts(List<Product> products, List<MenuProductCreateRequest> requests) {
        if (products.size() == requests.size()) {
            return;
        }
        Set<Long> notExistProductIds = getNotExistsProductIds(products, requests);
        throw new KitchenPosException("존재하지 않는 상품이 있습니다. notExistProductIds=" + notExistProductIds);
    }

    private Set<Long> getNotExistsProductIds(List<Product> products, List<MenuProductCreateRequest> requests) {
        Set<Long> existProductIds = products.stream()
            .map(Product::getId)
            .collect(toSet());
        Set<Long> requestProductIds = requests.stream()
            .map(MenuProductCreateRequest::getProductId)
            .collect(toSet());
        requestProductIds.removeAll(existProductIds);
        return requestProductIds;
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> findAll() {
        return menuRepository.findAll().stream()
            .map(MenuResponse::from)
            .collect(toList());
    }
}
